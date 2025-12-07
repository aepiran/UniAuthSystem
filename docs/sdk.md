## SDK/Client Library

// UniAuth Client SDK
@Component
public class UniAuthClient {

    private final RestTemplate restTemplate;
    private final String uniauthBaseUrl;
    
    public boolean checkPermission(String token, String permissionCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        PermissionCheckDTO request = new PermissionCheckDTO(
            getUsernameFromToken(token),
            getSystemCodeFromConfig(),
            permissionCode
        );
        
        ResponseEntity<Boolean> response = restTemplate.postForEntity(
            uniauthBaseUrl + "/api/v1/permissions/check",
            new HttpEntity<>(request, headers),
            Boolean.class
        );
        
        return Boolean.TRUE.equals(response.getBody());
    }
}