package springsideproject1.springsideproject1build.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import springsideproject1.springsideproject1build.domain.Member;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static springsideproject1.springsideproject1build.Utility.createTestMember;

@SpringBootTest
@Transactional
class MemberServiceJdbcTest {

    @Autowired
    MemberService memberService;

    private final JdbcTemplate jdbcTemplateTest;

    @Autowired
    public MemberServiceJdbcTest(DataSource dataSource) {
        jdbcTemplateTest = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    public void beforeEach() {
        resetTable("testmembers");
    }

    private void resetTable(String tableName) {
        jdbcTemplateTest.execute("DELETE FROM " + tableName);
        jdbcTemplateTest.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1");
    }

    @DisplayName("회원 가입 테스트")
    @Test
    public void membership() {
        // given
        Member member = createTestMember();

        // when
        memberService.joinMember(member);

        // then
        assertThat(memberService.findMembers().getFirst()).usingRecursiveComparison().isEqualTo(member);
    }

    @DisplayName("중복 ID를 사용하는 회원가입")
    @Test
    public void membershipWithSameID() {
        // given
        Member member1 = createTestMember();
        Member member2 = createTestMember();

        // when
        memberService.joinMember(member1);

        // then
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> memberService.joinMember(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 ID입니다.");
    }

    @DisplayName("잘못된 ID를 통한 회원 삭제")
    @Test
    public void removeByFaultID() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> memberService.removeMember("NoneID123"));
        assertThat(e.getMessage()).isEqualTo("해당 ID와 일치하는 회원이 없습니다.");
    }
}