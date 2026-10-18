package MyFirstTestAppSpringBoot.service;
import MyFirstTestAppSpringBoot.model.Positions;
import org.springframework.stereotype.Service;

@Service
public class QuarterlyBonusServiceImpl implements QuarterlyBonusService {
    private final AnnualBonusService annualBonusService;
    public QuarterlyBonusServiceImpl(AnnualBonusService annualBonusService) { this.annualBonusService = annualBonusService; }
    @Override
    public double calculate(Positions position, double salary, double bonus, int workDays) {
        if (position == null || !position.isManager()) {
            throw new IllegalArgumentException("Квартальная премия доступна только управленцам");
        }
        // Формула в задании не указана: принято 1/4 годовой премии.
        return annualBonusService.calculate(position, salary, bonus, workDays) / 4.0;
    }
}
