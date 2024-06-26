package springsideproject1.springsideproject1build.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import springsideproject1.springsideproject1build.domain.Company;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static springsideproject1.springsideproject1build.Utility.createSamsungElectronics;

@SpringBootTest
@Transactional
class CompanyServiceJdbcTest {

    @Autowired
    CompanyService companyService;

    private final JdbcTemplate jdbcTemplateTest;

    @Autowired
    public CompanyServiceJdbcTest(DataSource dataSource) {
        jdbcTemplateTest = new JdbcTemplate(dataSource);
    }

    @BeforeEach
    public void beforeEach() {
        resetTable("testcompanies");
    }

    private void resetTable(String tableName) {
        jdbcTemplateTest.execute("DELETE FROM " + tableName);
    }

    @DisplayName("중복 코드 번호를 사용하는 기업 등록")
    @Test
    public void registerCompanyWithSameCode() {
        // given
        Company company1 = createSamsungElectronics();
        Company company2 = createSamsungElectronics();

        // when
        companyService.joinCompany(company1);

        // then
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> companyService.joinCompany(company2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 코드 번호입니다.");
    }

    @DisplayName("존재하지 않는 코드 번호를 통한 기업 삭제")
    @Test
    public void removeCompanyByFaultCode() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> companyService.removeCompany("123456"));
        assertThat(e.getMessage()).isEqualTo("해당 코드 번호와 일치하는 기업이 없습니다.");
    }
}