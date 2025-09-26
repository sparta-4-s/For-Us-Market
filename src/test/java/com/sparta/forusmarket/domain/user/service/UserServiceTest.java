package com.sparta.forusmarket.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.sparta.forusmarket.domain.user.dto.response.UserResponse;
import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void 회원_프로필_조회에_성공한다() throws Exception {
        //given
        User user = new User(
                "test@test.com",
                "test",
                "test1234!",
                new Address(
                        "tc",
                        "ts",
                        "123-4"));
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        UserResponse userResponse = userService.getUserByIdU(user.getId());

        //then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.id()).isEqualTo(1L);
        assertThat(userResponse.email()).isEqualTo("test@test.com");
        assertThat(userResponse.name()).isEqualTo("test");
        assertThat(userResponse.address().getCity()).isEqualTo("tc");
        assertThat(userResponse.address().getStreet()).isEqualTo("ts");
        assertThat(userResponse.address().getZipcode()).isEqualTo("123-4");
    }
}
