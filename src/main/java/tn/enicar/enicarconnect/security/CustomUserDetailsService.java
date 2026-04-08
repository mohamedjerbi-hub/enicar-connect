package tn.enicar.enicarconnect.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.config.RolePermissionConfig;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RolePermissionConfig rolePermissionConfig;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));

        // Combine ROLE_xxx + individual permissions as authorities
        List<SimpleGrantedAuthority> authorities = Stream.concat(
                Stream.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                rolePermissionConfig.getPermissions(user.getRole()).stream()
                        .map(p -> new SimpleGrantedAuthority(p.name()))
        ).toList();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
