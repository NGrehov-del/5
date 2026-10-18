package MyFirstTestAppSpringBoot.service;
import MyFirstTestAppSpringBoot.model.Positions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class AnnualBonusServiceImplTest {
    static AnnualBonusServiceImpl inYear(int year) {
        return new AnnualBonusServiceImpl(Clock.fixed(LocalDate.of(year, 6, 1).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
    }
    @Test
    void calculate() {
        // given: пример из скриншота методички, невисокосный год.
        Positions position = Positions.HR;
        double bonus = 2.0;
        int workDays = 243;
        double salary = 100000.00;
        // when
        double result = inYear(2023).calculate(position, salary, bonus, workDays);
        // then
        assertEquals(360493.8271604938, result, 0.000001);
    }
    @ParameterizedTest
    @CsvSource({"2023,365", "2024,366", "2000,366", "2100,365"})
    void calculatesRealNumberOfDays(int year, int days) {
        assertEquals(days * 1.2, inYear(year).calculate(Positions.HR, 1, 1, 1), 0.000001);
    }
    @Test
    void allowsLastDayOfLeapYear() {
        assertEquals(120000, inYear(2024).calculate(Positions.HR, 100000, 1, 366), 0.000001);
    }
    @Test
    void rejectsDay366InCommonYear() {
        assertThrows(IllegalArgumentException.class, () -> inYear(2023).calculate(Positions.HR, 100000, 1, 366));
    }
    @Test
    void zeroBonusReturnsZero() {
        assertEquals(0, inYear(2023).calculate(Positions.HR, 100000, 0, 243));
    }
    @ParameterizedTest
    @CsvSource({"0,1,243", "-1,1,243", "100000,-1,243", "100000,1,0", "100000,1,-1", "100000,1,367"})
    void rejectsInvalidArguments(double salary, double bonus, int days) {
        assertThrows(IllegalArgumentException.class, () -> inYear(2023).calculate(Positions.HR, salary, bonus, days));
    }
    @Test
    void rejectsMissingPositionAndNonFiniteNumbers() {
        var service = inYear(2023);
        assertThrows(IllegalArgumentException.class, () -> service.calculate(null, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> service.calculate(Positions.HR, Double.NaN, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> service.calculate(Positions.HR, 1, Double.POSITIVE_INFINITY, 1));
        assertThrows(IllegalArgumentException.class, () -> service.calculate(Positions.HR, Double.MAX_VALUE, 2, 1));
    }
}
