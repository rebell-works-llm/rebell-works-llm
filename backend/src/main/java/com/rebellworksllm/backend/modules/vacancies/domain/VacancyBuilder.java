package com.rebellworksllm.backend.modules.vacancies.domain;

public class VacancyBuilder {

    private final String id;
    private String title;
    private String description;
    private String salary;
    private String workingHours;
    private String function;
    private String link;
    double priority;
    int matchCount;

    public VacancyBuilder(String id) {
        this.id = id;
    }

    public VacancyBuilder title(String title) {
        this.title = title;
        return this;
    }

    public VacancyBuilder description(String description) {
        this.description = description;
        return this;
    }

    public VacancyBuilder salary(String salary) {
        this.salary = salary;
        return this;
    }

    public VacancyBuilder workingHours(String workingHours) {
        this.workingHours = workingHours;
        return this;
    }

    public VacancyBuilder function(String function) {
        this.function = function;
        return this;
    }

    public VacancyBuilder priority(double priority) {
        this.priority = priority;
        return this;
    }

    public VacancyBuilder matchCount(int matchCount) {
        this.matchCount = matchCount;
        return this;
    }

    public VacancyBuilder link(String link) {
        this.link = link;
        return this;
    }

    public Vacancy build() {
        Vacancy vacancy = new Vacancy(id);
        vacancy.setTitle(title);
        vacancy.setDescription(description);
        vacancy.setSalary(salary);
        vacancy.setWorkingHours(workingHours);
        vacancy.setFunction(function);
        vacancy.setPriority(priority);
        vacancy.setMatchCount(matchCount);
        vacancy.setLink(link);
        return vacancy;
    }
}
