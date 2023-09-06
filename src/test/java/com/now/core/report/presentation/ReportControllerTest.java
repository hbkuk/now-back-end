package com.now.core.report.presentation;

import com.now.config.document.utils.RestDocsTestSupport;
import com.now.core.authentication.application.JwtTokenService;
import com.now.core.authentication.constants.Authority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import static com.now.config.document.snippet.RequestCookiesSnippet.cookieWithName;
import static com.now.config.document.snippet.RequestCookiesSnippet.customRequestHeaderCookies;
import static com.now.config.document.utils.RestDocsConfig.field;
import static com.now.config.fixtures.comment.CommentFixture.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@DisplayName("Report 컨트롤러는")
class ReportControllerTest extends RestDocsTestSupport {
    // TODO: TEST CODE
}