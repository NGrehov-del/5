package MyFirstTestAppSpringBoot.service;
import MyFirstTestAppSpringBoot.model.Positions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

class QuarterlyBonusServiceImplTest {
    private final QuarterlyBonusService service = new QuarterlyBonusServiceImpl(AnnualBonusServiceImplTest.inYear(2023));
    @ParameterizedTest
    @CsvSource({"TL,195267.48971193415", "PO,210288.06584362139", "TPM,225308.64197530865", "CTO,262860.08230452676"})
    void calculatesBonusForManagers(Positions position, double expected) {
        assertTrue(position.isManager());
        assertEquals(expected, service.calculate(position, 100000, 2, 243), 0.000001);
    }
    @ParameterizedTest
    @EnumSource(value = Positions.class, names = {"DEV", "HR"})
    void rejectsNonManagers(Positions position) {
        assertFalse(position.isManager());
        assertThrows(IllegalArgumentException.class, () -> service.calculate(position, 100000, 2, 243));
    }
    @Test
    void rejectsMissingPosition() {
        assertThrows(IllegalArgumentException.class, () -> service.calculate(null, 100000, 2, 243));
    }
    @Test
    void validatesInputForManagers() {
        assertThrows(IllegalArgumentException.class, () -> service.calculate(Positions.TL, 100000, 2, 0));
    }
    @Test
    void calculatesZeroAndLeapYear() {
        assertEquals(0, service.calculate(Positions.TL, 100000, 0, 243));
        var leapService = new QuarterlyBonusServiceImpl(AnnualBonusServiceImplTest.inYear(2024));
        assertEquals(65000, leapService.calculate(Positions.TL, 100000, 1, 366), 0.000001);
    }
}
