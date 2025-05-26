package kiricasa.programa.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kiricasa.programa.models.UsuarioModel;



@Service
public class JwtService {
    //clave secreta para encriptar el token
    //en este caso la clave secreta es una cadena de caracteres aleatoria
    //en un caso real esta clave secreta deberia ser almacenada en un lugar seguro
    //y no deberia ser hardcodeada en el codigo fuente
    private final String SECRET_KEY = "bWktc3VwZXItY2xhdmUtc2VjcmV0YS0xMjM0NTY3ODkw";
    @Value("${jwt.expiration}") // duración en milisegundos
    private long jwtExpiration;
    /**
     * metodo que devuelve el token de un usuario
     *  */
    public String getToken(UserDetails usuario) {
        return getToken(new HashMap<>(), usuario);
    }

    /**
     * metodo que devuelve el token de un usuario details
     * @param usuario
     * @return token
     */
    public String generateToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails);
    }

    /**
     * metodo que devuelve el token de un usuario model
     * @param usuario
     * @return token
     */
    public String generateToken(UsuarioModel usuario) {
        return buildToken(new HashMap<>(), usuario);
    }
    /**
     * metodo que devuelve el token creado de un usuario details
     * @param extraClaims
     * @param usuario
     * @return token
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     *  metido que genera el token con el uso de la libreria Jwts con el algoritmo HS256
     * @param extraClaims
     * @param usuario
     * @return token
     */
    private String getToken(HashMap<String, Object> extraClaims, UserDetails usuario) {



        return Jwts.builder().setClaims(extraClaims)
                .setSubject(usuario.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30
                ))
                .signWith(getKey(),SignatureAlgorithm.HS256).compact();
    }

        /**
         * metodo que encripa la clave secreta que hemos definido en la variable
         * @return
         */
        private Key getKey() {
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }

        public String getUsernameFromToken(String token) {
            return getClaim(token, Claims::getSubject);
        }

        public boolean isTokenValid(String token, UserDetails userDetails) {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }
        public boolean isTokenValid(String token) {
            String username = getUsernameFromToken(token);
            return (username != null && !isTokenExpired(token));
        }

        private Claims getAllClaims(String token){
            return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
        }
        public <T> T getClaim(String token,Function <Claims,T> claimsResolver) {
           final  Claims claims = getAllClaims(token);
            return claimsResolver.apply(claims);
        }
        private Date getExpiration(String token) {
            return getClaim(token, Claims::getExpiration);
        }
        private boolean isTokenExpired(String token) {
            return getExpiration(token).before(new Date(System.currentTimeMillis()));
        }


}
