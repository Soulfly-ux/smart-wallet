package app.security;

import app.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@AllArgsConstructor
@Builder
public class AuthenticationDetails implements UserDetails {

   private final UUID userId;
   private final String username;
   private final String password;
   private final Role role;
   private final boolean isActive;

    // Този метод се използва за да върнем списък от авторизации, т.е. роли, текущия потребител има.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // не е задължително да се ползва този клас, но това е по-просто и е по-подходящо за този случай:
       // SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name()); // -> АКО ИЗПОЛЗВАМ .hasAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());// -> АКО ИЗПОЛЗВАМ .hasRole в метод контролерите или конфигурацията

        return List.of(authority);// връща списък, защото потребителя може да има повече от една роля
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
