package MyFirstTestAppSpringBoot.service;
import MyFirstTestAppSpringBoot.model.Positions;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.Year;

@Service
public class AnnualBonusServiceImpl implements AnnualBonusService {
    private final Clock clock;
    public AnnualBonusServiceImpl() { this(Clock.systemUTC()); }
    public AnnualBonusServiceImpl(Clock clock) { this.clock = clock; }
    @Override
    public double calculate(Positions position, double salary, double bonus, int workDays) {
        int daysInYear = Year.now(clock).length();
        if (position == null || !Double.isFinite(salary) || salary <= 0 || !Double.isFinite(bonus)
                || bonus < 0 || workDays < 1 || workDays > daysInYear) {
            throw new IllegalArgumentException("Нужны должность, положительный оклад, неотрицательный бонус и workDays от 1 до " + daysInYear);
        }

        double result = salary * bonus * daysInYear * position.getPositionCoefficient() / workDays;
        if (!Double.isFinite(result)) throw new IllegalArgumentException("Результат выходит за диапазон double");
        return result;
    }
}
