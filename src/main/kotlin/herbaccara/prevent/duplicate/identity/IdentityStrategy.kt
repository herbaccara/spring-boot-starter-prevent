package herbaccara.prevent.duplicate.identity

import jakarta.servlet.http.HttpServletRequest
import java.util.function.Function

interface IdentityStrategy : Function<HttpServletRequest, String?>
