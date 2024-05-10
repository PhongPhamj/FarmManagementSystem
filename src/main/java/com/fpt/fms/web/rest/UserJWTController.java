package com.fpt.fms.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fpt.fms.security.jwt.JWTFilter;
import com.fpt.fms.security.jwt.TokenProvider;
import com.fpt.fms.web.rest.vm.LoginVM;
import javax.validation.Valid;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;


    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());
        Claims claims = tokenProvider.extractClaimsFromToken(jwt);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt, claims.get("name",String.class),  claims.getSubject(), claims.get("id",Long.class), claims.get("farm-role", String.class), claims.get("auth", String.class)), httpHeaders, HttpStatus.OK);
    }
    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        private String fullName;

        private String username;

        private Long id;

        private String auths;
        private String farmRole;

        public JWTToken(String idToken, String fullName, String username, Long id, String farmRole, String auths) {
            this.idToken = idToken;
            this.auths = auths;
            this.fullName = fullName;
            this.username = username;
            this.id = id;
            this.farmRole = farmRole;
        }

        public String getAuths() {
            return auths;
        }

        public void setAuths(String auths) {
            this.auths = auths;
        }

        public String getFarmRole() {
            return farmRole;
        }

        public void setFarmRole(String farmRole) {
            this.farmRole = farmRole;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }

    }
}
