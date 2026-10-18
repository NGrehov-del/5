package MyFirstTestAppSpringBoot.service;
import MyFirstTestAppSpringBoot.model.Positions;
public interface QuarterlyBonusService {
    double calculate(Positions position, double salary, double bonus, int workDays);
}
