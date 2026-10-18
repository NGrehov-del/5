package MyFirstTestAppSpringBoot.service;
import MyFirstTestAppSpringBoot.model.Positions;

public interface AnnualBonusService {
    double calculate(Positions position, double salary, double bonus, int workDays);
}
