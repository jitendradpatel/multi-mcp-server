package com.example.mcp.jwe;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class JweUtils {

    private static final String PRIVATE_KEY_PATH = "keys/private.pem";
    private static final String PUBLIC_KEY_PATH = "keys/public.pem";

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public JweUtils() throws Exception {
        publicKey = loadPublicKeyFromResources(PUBLIC_KEY_PATH);
        privateKey = loadPrivateKeyFromResources(PRIVATE_KEY_PATH);
    }

    public JweUtils(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public static JweUtils getInstance() throws Exception {
        return new JweUtils();
    }

    // ✅ Encrypt (JWE) with roles
    public String generateEncryptedToken(String subject, String email, List<String> roles, long expirySeconds) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .claim("email", email)
                .claim("roles", roles) // add roles claim
                .expirationTime(new Date(new Date().getTime() + expirySeconds * 1000))
                .issuer("local-test")
                .build();

        JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM);
        EncryptedJWT jwt = new EncryptedJWT(header, claims);
        jwt.encrypt(new RSAEncrypter(publicKey));

        return jwt.serialize();
    }

    // ✅ Decrypt
    public JWTClaimsSet decryptToken(String token) throws Exception {
        EncryptedJWT jwt = EncryptedJWT.parse(token);
        jwt.decrypt(new RSADecrypter(privateKey));
        return jwt.getJWTClaimsSet();
    }

    // ✅ Load public.pem from resources
    public static RSAPublicKey loadPublicKeyFromResources(String path) throws Exception {
        String pem = PemUtils.readKeyFromClasspath(path)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] bytes = Base64.getDecoder().decode(pem);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }

    // ✅ Load private.pem from resources
    public static RSAPrivateKey loadPrivateKeyFromResources(String path) throws Exception {
        String pem = PemUtils.readKeyFromClasspath(path)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] bytes = Base64.getDecoder().decode(pem);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    // Load PEM public key
    public static RSAPublicKey loadPublicKey(String path) throws Exception {
        String pem = Files.readString(Path.of(path))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] bytes = Base64.getDecoder().decode(pem);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }

    // Load PEM private key
    public static RSAPrivateKey loadPrivateKey(String path) throws Exception {
        String pem = Files.readString(Path.of(path))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] bytes = Base64.getDecoder().decode(pem);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }
}
