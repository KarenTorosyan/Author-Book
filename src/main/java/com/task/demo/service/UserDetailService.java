package com.task.demo.service;
        import com.task.demo.model.Author;
        import com.task.demo.repository.AuthorRepository;
        import com.task.demo.security.CurrentUser;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.security.core.userdetails.UserDetailsService;
        import org.springframework.security.core.userdetails.UsernameNotFoundException;
        import org.springframework.stereotype.Service;
        import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Author> email = authorRepository.findByEmail(s);
        if(!email.isPresent()){
            throw new UsernameNotFoundException("");
        }
        return new CurrentUser(email.get());
    }
}
