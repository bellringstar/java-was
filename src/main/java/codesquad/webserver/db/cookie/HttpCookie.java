package codesquad.webserver.db.cookie;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpCookie {
    private String name;
    private String value;
    private String domain;
    private String path;
    private ZonedDateTime expires;
    private Integer maxAge;
    private boolean secure;
    private boolean httpOnly;
    private SameSite sameSite;
    private Map<String, String> additionalAttributes = new HashMap<>();

    private static final Integer MAX_AGE = 30 * 60 * 1000;

    public enum SameSite {
        STRICT, LAX, NONE;
    }

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
        this.path = "/";
        this.maxAge = MAX_AGE;
    }

    public HttpCookie() {

    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public ZonedDateTime getExpires() {
        return expires;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public SameSite getSameSite() {
        return sameSite;
    }

    public Map<String, String> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public HttpCookie setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public HttpCookie setPath(String path) {
        this.path = path;
        return this;
    }

    public HttpCookie setExpires(ZonedDateTime expires) {
        this.expires = expires;
        return this;
    }

    public HttpCookie setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public HttpCookie setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public HttpCookie setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public HttpCookie setSameSite(SameSite sameSite) {
        this.sameSite = sameSite;
        return this;
    }

    public HttpCookie setAttribute(String key, String value) {
        additionalAttributes.put(key, value);
        return this;
    }

    public String toSetCookieHeader() {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name).append("=").append(value);
        }

        if (domain != null) {
            if (sb.isEmpty()) {
                sb.append("Domain=").append(domain);
            } else {
                sb.append("; Domain=").append(domain);
            }
        }
        if (path != null) {
            if (sb.isEmpty()) {
                sb.append("Path=").append(path);
            } else {
                sb.append("; Path=").append(path);
            }
        }
        if (expires != null) {
            if (sb.isEmpty()) {
                sb.append("Expires=").append(expires);
            } else {
                sb.append("; Expires=").append(expires);
            }
        }
        if (maxAge != null) {
            if (sb.isEmpty()) {
                sb.append("Max-Age=").append(maxAge);
            } else {
                sb.append("; Max-Age=").append(maxAge);
            }
        }
        if (secure) {
            if (sb.isEmpty()) {
                sb.append("Secure=").append(secure);
            } else {
                sb.append("; Secure");
            }
        }
        if (httpOnly) {
            if (sb.isEmpty()) {
                sb.append("HttpOnly");
            } else {
                sb.append("; HttpOnly");
            }
        }
        if (sameSite != null) {
            if (sb.isEmpty()) {
                sb.append("SameSite=").append(sameSite);
            } else {
                sb.append("; SameSite=").append(sameSite);
            }
        }

        for (Entry<String, String> entry : additionalAttributes.entrySet()) {
            sb.append("; ").append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sb.toString();
    }
}
