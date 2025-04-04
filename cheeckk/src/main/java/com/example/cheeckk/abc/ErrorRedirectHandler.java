import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorRedirectHandler {

    private final BizDomainService bizDomainService;

    public ErrorRedirectHandler(BizDomainService bizDomainService) {
        this.bizDomainService = bizDomainService;
    }

    private void redirectToErrorPage(HttpServletRequest request, HttpServletResponse response, String errorKey) throws IOException {

    OAuth2Helper oauthHelper = new OAuth2Helper(bizDomainService);
    String returnUrl = oauthHelper.getLogoutUrl(request);
    String encodedReturnUrl = returnUrl != null ? URLEncoder.encode(returnUrl, StandardCharsets.UTF_8.toString()) : "";

    Util.deleteCookie(Constants.AC_SIGNON_MODULE_COOKIE, response, request.getCookies());
    Util.deleteCookie(Constants.AC_SIGNOFF_COOKIE, response, request.getCookies());
    Util.deleteCookie(Constants.AC_SWITCH_AGENCY_COOKIE, response, request.getCookies());
    Util.deleteCookie(Constants.ACCELA_AUTH_COOKIE, response, request.getCookies());
    Util.deleteCookie(Constants.LASTEST_REQUEST_TIME, response, request.getCookies());
    Util.deleteCookie(Constants.ORIGINAL_PARENT_LOGGEDIN_AGENCY, response, request.getCookies());
    Util.deleteCookie(Constants.ORIGINAL_PARENT_LOGGEDIN_USER, response, request.getCookies());
    Util.deleteCookie("JSESSIONID", response, request.getCookies(), true);

    response.getWriter().write(String.format(
              "<script>"
                      + "localStorage.clear();"
                      + "sessionStorage.clear();"
                      + "window.top.location='/security/ValidateSSOPage.jsp?"+errorKey+"=true&needRedirect=true&traceId=%s&redirectToLoginUrl=%s';"
                      + "</script>",
              Encode.forJavaScript(AVLogUtil.getTraceId()),
              Encode.forJavaScript(encodedReturnUrl)
      ));
  }
}
