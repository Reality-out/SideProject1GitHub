package springsideproject1.springsideproject1build.controller.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import springsideproject1.springsideproject1build.domain.Member;
import springsideproject1.springsideproject1build.service.MemberService;

import javax.sql.DataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static springsideproject1.springsideproject1build.Utility.createTestMember;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ManagerMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    private final JdbcTemplate jdbcTemplateTest;

    @Autowired
    public ManagerMemberControllerTest(DataSource dataSource) {
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

    @DisplayName("멤버 리스트 페이지 접속")
    @Test
    public void accessMembersPage() throws Exception {
        mockMvc.perform(get("/manager/member/showAll"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/select/membersPage"));
    }

    @DisplayName("회원 탈퇴 페이지 접속")
    @Test
    public void accessMembershipWithdrawPage() throws Exception {
        mockMvc.perform(get("/manager/member/remove"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/remove/membership/processPage"));
    }

    @DisplayName("회원 탈퇴 완료 페이지 접속")
    @Test
    public void accessMembershipWithdrawFinishPage() throws Exception {
        // given
        Member member = createTestMember();

        // when
        memberService.joinMember(member);

        // then
        String id = member.getId();

        mockMvc.perform(post("/manager/member/remove")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", id))
                .andExpect(status().isSeeOther());

        mockMvc.perform(get("/manager/member/remove/finish")
                        .param("id", id))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/remove/membership/finishPage"));
    }
}