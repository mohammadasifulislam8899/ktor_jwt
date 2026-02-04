package com.droidneststudio.auth.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService : TokenService {
    
    override fun generateAccessToken(config: TokenConfig, vararg claims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.accessTokenExpiryMs))
            .withIssuedAt(Date())
            .withJWTId(UUID.randomUUID().toString())
        
        claims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        
        return token.sign(Algorithm.HMAC256(config.secret))
    }
    
    override fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }
}