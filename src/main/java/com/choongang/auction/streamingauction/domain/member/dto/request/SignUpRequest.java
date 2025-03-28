package com.choongang.auction.streamingauction.domain.member.dto.request;


import com.choongang.auction.streamingauction.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "^(01[016789])[0-9]{3,4}[0-9]{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String phone;

    @NotBlank(message = "사용자 을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9._]{4,20}$",
            message = "아이디는 4-20자의 영문, 숫자 사용 가능합니다")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상, 영문과 숫자 조합이어야 합니다")
    private String password;

    // 클라이언트가 전송한 입력값들을 엔터티로 변환
    public Member toEntity() {

        // 이메일과 휴대전화번호를 구분해서 처리
        String email = null;


        if (this.email.contains("@")) {
            email = this.email;
        }

        return Member.builder()
                .email(email)
                .username(this.username)
                .name(this.name)
                .phone(this.phone)
                .password(this.password)
                .build();
    }

}
