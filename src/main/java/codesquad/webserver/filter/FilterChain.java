package codesquad.webserver.filter;

import codesquad.webserver.annotation.Component;
import codesquad.webserver.httprequest.HttpRequest;
import codesquad.webserver.httpresponse.HttpResponse;
import codesquad.webserver.httpresponse.HttpResponseBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class FilterChain {
    private final List<OrderedFilter> filters = new ArrayList<>();
    private final ThreadLocal<Integer> currentFilterIndex = ThreadLocal.withInitial(() -> 0);
    private final ThreadLocal<HttpResponse> currentResponse = new ThreadLocal<>();

    public void addFilter(Filter filter, int order) {
        filters.add(new OrderedFilter(filter, order));
        filters.sort(Comparator.comparingInt(OrderedFilter::getOrder));
    }

    public HttpResponse doFilter(HttpRequest request) {
        currentFilterIndex.set(0);
        currentResponse.set(HttpResponseBuilder.ok().build());

        try {
            processFilters(request);
        } finally {
            HttpResponse response = currentResponse.get();
            currentFilterIndex.remove();
            currentResponse.remove();
            return response;
        }
    }

    public List<OrderedFilter> getFilters() {
        return filters;
    }

    public void setResponse(HttpResponse response) {
        currentResponse.set(response);
    }

    private void processFilters(HttpRequest request) {
        while (currentFilterIndex.get() < filters.size()) {
            Filter currentFilter = filters.get(currentFilterIndex.get()).getFilter();
            currentFilter.doFilter(request, currentResponse.get(), this);

            if (currentResponse.get().getStatusCode() != 200) {
                break;
            }

            currentFilterIndex.set(currentFilterIndex.get() + 1);
        }
    }

    private static class OrderedFilter {
        private final Filter filter;
        private final int order;

        public OrderedFilter(Filter filter, int order) {
            this.filter = filter;
            this.order = order;
        }

        Filter getFilter() {
            return filter;
        }

        int getOrder() {
            return order;
        }
    }
}