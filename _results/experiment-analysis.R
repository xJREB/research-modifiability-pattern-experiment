########################################################################
# Experiment Results Evaluation
########################################################################

# Delete current environment variables --------
rm(list = ls(all.names = TRUE))

# Load required packages -------
library(dplyr)
library(ggplot2)
library(tidyr)
library(corrplot)
library(Hmisc)
library(DescTools)
library(exactRankTests)

# Helper function to visually and statistically check the distribution of a data set
checkDataDistribution <- function(data){
  hist(data)
  # Shapiro-Wilk test for normal distribution
  # Null hypothesis with Shapiro-Wilk test is that "data" came from a normally distributed population,
  # i.e. p-value <= 0.05 --> "data" is not normally distributed
  shapiro.test(data)
}

# Read data ------------
data <- read.csv("data.csv")
str(data)
summary(data)

# Transform data to a better format for ggplot
dataDuration <- data %>%
  # Only use participants with effectiveness >= 66%
  # filter(!is.na(ex2Duration)) %>%
  select(pId, version, ex1Duration, ex2Duration, ex3Duration) %>% 
  gather(key = "exercise", value = "duration", ex1Duration, ex2Duration, ex3Duration) %>% 
  mutate(exercise = gsub("ex", "", exercise)) %>% 
  mutate(exercise = as.integer(gsub("Duration", "", exercise))) %>% 
  arrange(pId, exercise)

# Compare groups --------

# Group version 1: non-pattern version (control group)
# Group version 2: pattern version (treatment group)

groupComparison <- 
  data %>%
  # Only use participants with effectiveness >= 66%
  # filter(!is.na(ex2Duration)) %>%
  group_by(version) %>% 
  summarise(
    numParticipants = n(),
    # current semester
    avgSemester = mean(semester),
    sdSemester = sd(semester),
    # Percentage of participants that attended the introductory presentation
    introAttendance = mean(intro),
    # Self-reported difficulty for exercise1 (1-10)
    avgDiffEx1 = mean(diff.ex1, na.rm = TRUE),
    sdDiffEx1 = sd(diff.ex1, na.rm = TRUE),
    # Self-reported difficulty for exercise2 (1-10)
    avgDiffEx2 = mean(diff.ex2, na.rm = TRUE),
    sdDiffEx2 = sd(diff.ex2, na.rm = TRUE),
    # Self-reported difficulty for exercise3 (1-10)
    avgDiffEx3 = mean(diff.ex3, na.rm = TRUE),
    sdDiffEx3 = sd(diff.ex3, na.rm = TRUE),
    # Combined AVG self-reported difficulty (1-10)
    avgDiff = (avgDiffEx1 + avgDiffEx2 + avgDiffEx3) / 3,
    # Programming experience in years
    avgYearsOfProgramming = mean(yearsOfProgramming),
    sdYearsOfProgramming = sd(yearsOfProgramming),
    # Self-reported skill/experience with Java (1-10)
    avgJava = mean(expert.java),
    sdJava = sd(expert.java),
    # Self-reported skill/experience with web development (1-10)
    avgWeb = mean(expert.web),
    sdWeb = sd(expert.web),
    # Self-reported skill/experience with service-based systems (1-10)
    avgSbs = mean(expert.sbs),
    sdSbs = sd(expert.sbs),
    # Self-reported skill/experience with design patterns (1-10)
    avgPatterns = mean(expert.patterns),
    sdPatterns = sd(expert.patterns),
    # Self-reported skill/experience with service-based design patterns (1-10)
    avgSpatterns = mean(expert.spatterns),
    sdSpatterns = sd(expert.spatterns),
    # Combined AVG self-reported skill/experience (1-10)
    avgSkill = (avgJava + avgWeb + avgSbs + avgPatterns + avgSpatterns) / 5,
    # AVG effectiveness in percent for the 3 exercises (0, 1/3, 2/3, 1)
    avgEffectiveness = mean(effectiveness),
    sdEffectiveness = sd(effectiveness),
    # Number of participants that solved exercise1
    solvedEx1 = sum(!is.na(ex1Duration)),
    # Number of participants that solved exercise2
    solvedEx2 = sum(!is.na(ex2Duration)),
    # Number of participants that solved exercise3
    solvedEx3 = sum(!is.na(ex3Duration)),
    # Duration for exercise1
    avgDurationEx1 = mean(ex1Duration, na.rm = TRUE),
    sdDurationEx1 = sd(ex1Duration, na.rm = TRUE),
    # Duration for exercise2
    avgDurationEx2 = mean(ex2Duration, na.rm = TRUE),
    sdDurationEx2 = sd(ex2Duration, na.rm = TRUE),
    # Duration for exercise3
    avgDurationEx3 = mean(ex3Duration, na.rm = TRUE),
    sdDurationEx3 = sd(ex3Duration, na.rm = TRUE)
  )
# Duration per task over all 3 exercises
groupComparison$avgDurationPerEx <- aggregate(duration ~ version, dataDuration, mean)$duration
groupComparison$sdDurationPerEx <- aggregate(duration ~ version, dataDuration, sd)$duration

# Duration to complete all 3 exercises (total)
data %>%
  filter(!is.na(ex3Duration)) %>% 
  mutate(totalDuration = ex1Duration + ex2Duration + ex3Duration) %>% 
  group_by(version) %>% 
  summarise(
    avgTotalDuration = mean(totalDuration),
    sdTotalDuration = sd(totalDuration)
  )

# Analyze study program distributions per group
# Group 1
data %>%
  filter(version == 1) %>%
  select(study) %>%
  summary

# Group 2
data %>%
  filter(version == 2) %>%
  select(study) %>%
  summary

# --> Groups are roughly equal w.r.t. study programs, skill/experience, and effectiveness
# Minor differences:
# - ~14% more attended the intro for version 1
# - Version 2 participants had ~ half a year more programming experience
# - Version 2 participants had ~ 0.7 points more in self-reported skill/experience (1-10)


# Analysis of effectiveness and efficiency ------

trimPercent = 0.0   # optionally cut off the best and worst x percent

# Create distinct data sets to analyze the distribution

# Effectiveness groups:
effectivenes_group1 = data %>%
  filter(version == 1) %>%
  select(effectiveness) %>% 
  Trim(trim = trimPercent, na.rm = TRUE)

effectivenes_group2 = data %>% 
  filter(version == 2) %>%
  select(effectiveness) %>% 
  Trim(trim = trimPercent, na.rm = TRUE)

exNum = c(1,2,3)    # select exercises

# Efficiency groups:
efficiency_group1 = dataDuration %>%
  filter(version == 1) %>%
  filter(exercise %in% exNum) %>%
  select(duration) %>% 
  Trim(trim = trimPercent, na.rm = TRUE)

efficiency_group2 = dataDuration %>% 
  filter(version == 2) %>%
  filter(exercise %in% exNum) %>%
  select(duration) %>% 
  Trim(trim = trimPercent, na.rm = TRUE)

# Check distribution of all data sets
checkDataDistribution(effectivenes_group1)
checkDataDistribution(effectivenes_group2)
checkDataDistribution(efficiency_group1)
checkDataDistribution(efficiency_group2)

# --> All data sets are NOT normally distributed (all p-values << 0.05), i.e. t-test cannot be used
# Instead, a non-parametric test for non-normality is needed
# --> Wilcoxon–Mann–Whitney test is used 
# Alternative: Kolmogorov–Smirnov test


# Test for effectiveness
# Hypothesis: effectiveness of pattern version #2 is greater than for the non-pattern version #1

# Calculate Wilcoxon–Mann–Whitney test
# Standard implementation from the `stats` package (asymptotic approximation with ties)
wilcox.test(
  x = effectivenes_group2,
  y = effectivenes_group1,
  alternative = "greater",
  conf.level = 0.99
)
# --> p-value: 0.5939

# Calculate Exact Wilcoxon–Mann–Whitney test
# Implementation that adjusts for ties (package: `exactRankTests`, differences are only very small though)
wilcox.exact(
  x = effectivenes_group2,
  y = effectivenes_group1,
  alternative = "greater",
  conf.level = 0.99
)
# --> p-value: 0.5903

# Calculate Kolmogorov–Smirnov test
ks.test(
  x = effectivenes_group1,
  y = effectivenes_group2,
  alternative = "greater",
  conf.level = 0.99
)
# --> p-value: 0.6663

# Conclusion:
# --> no significant p for in all test variants
# --> null hypothesis ("means are similar") cannot be rejected
 

# Test for efficiency
# Hypotheses: exercise durations for pattern version #2 are less than for the non-pattern version #1

# Calculate Wilcoxon–Mann–Whitney test
# Standard implementation from the `stats` package is sufficient (no ties)
wilcox.test(
  x = efficiency_group2,
  y = efficiency_group1,
  alternative = "less",
  conf.level = 0.99
)
# All exercises at once (exNum = c(1,2,3)):
# --> no significant p: 0.0496 (this is barely below 0.05, but with Bonferroni correction, we need p <= 0.01)
# Exercises 1 and 2 together (exNum = c(1,2)):
# --> no significant p: 0.4018
# Exercises 1 and 3 together (exNum = c(1,3)):
# --> no significant p: 0.1291
# Exercises 2 and 3 together (exNum = c(2,3)):
# --> highly significant p: 0.0009678
# Only exercise 1 (exNum = c(1)):
# --> no significant p: 0.741
# Only exercise 2 (exNum = c(2)):
# --> no significant p: 0.08194
# Only exercise 3 (exNum = c(3)):
# --> highly significant p: 0.000617


# Calculate Kolmogorov–Smirnov test
ks.test(
  x = efficiency_group1,
  y = efficiency_group2,
  alternative = "less",
  conf.level = 0.99
)
# All exercises at once (exNum = c(1,2,3)):
# --> no significant p: 0.09141
# Exercises 1 and 2 together (exNum = c(1,2)):
# --> no significant p: 0.4426
# Exercises 1 and 3 together (exNum = c(1,3)):
# --> no significant p: 0.2733
# Exercises 2 and 3 together (exNum = c(2,3)):
# --> highly significant p: 0.00262
# Only exercise 1 (exNum = c(1)):
# --> no significant p: 0.952
# Only exercise 2 (exNum = c(2)):
# --> no significant p: 0.2084
# Only exercise 3 (exNum = c(3)):
# --> highly significant p: 0.005141

# Conclusion:
# --> both tests only show a significant p-value (<= 0.01) in the case of
#     - exercise 3 alone
#     - exercise 2 and 3 combined
# in all other cases, the null hypotheses cannot be rejected


# Create box plot to visually compare duration of versions

# Create tooltips
meanValueToolTips <- dataDuration %>% 
  mutate(exercise = factor(exercise, levels = c(1,2,3), labels = c("#1: Process Abstraction", "#2: Service Facade", "#3: Event-Driven Messaging"))) %>% 
  group_by(version, exercise) %>% 
  select(version, exercise, duration) %>% 
  summarise(duration = round(mean(duration, na.rm = TRUE)))
# Draw the plot
dataDuration %>%
  mutate(version = factor(version, levels = c(1,2), labels = c("#1 (no patterns)", "#2 (patterns)"))) %>%
  mutate(exercise = factor(exercise, levels = c(1,2,3), labels = c("#1: Process Abstraction", "#2: Service Facade", "#3: Event-Driven Messaging"))) %>% 
  ggplot(aes(x = version, y = duration, fill = exercise)) +
  geom_boxplot(na.rm = TRUE) +
  facet_grid(exercise ~ .) +
  labs(x = "Group / Version", y = "Duration in sec", fill = "Task Number") +
  theme(
    text = element_text(size = 12.6, face = "bold", family = "sans"),
    axis.title = element_text(size = 18),
    axis.text.x = element_text(size = 16),
    legend.title = element_text(size = 18),
    legend.text = element_text(size = 16)
  ) +
  geom_text(data = meanValueToolTips, aes(label = duration), nudge_x = -0.465, size = 4.8)


# Calculate correlation between version and duration
exNum = c(1,2,3)        # select exercises
# Kendall's Tau: more permissive w.r.t. assumptions (e.g. does not require monotonicity), less sensitive to ties
corr_mthd = "kendall"
dataDuration %>%
  filter(exercise %in% exNum) %>%
  mutate(exercise = as.integer(exercise)) %>% 
  summarise(
    correlation = cor.test(version, duration, method = corr_mthd, exact = FALSE)$estimate,
    p.value = cor.test(version, duration, method = corr_mthd, exact = FALSE)$p.value
  )
# --> no significant correlation for all exercises at once (corr = -0.143, p = 0.098)
# --> no significant correlation for exercises 1 and 2 together (corr = -0.024, p = 0.798)
# --> no significant correlation for exercises 1 and 3 together (corr = -0.117, p = 0.253)
# --> significant medium correlation for exercises 2 and 3 together (corr = -0.387, p = 0.0024)
# --> no significant correlation for exercise 1 alone (corr = 0.077, p = 0.521)
# --> no significant correlation for exercise 2 alone (corr = -0.237, p = 0.153)
# --> significant strong correlation for exercise 3 alone (corr = -0.635, p = 0.0025)

# --> similar to the tests: correlations are only significant for exercise 3 alone and exercises 2 and 3 combined


# Correlation matrix for exploration --------

# "pearson" for linear correlation of continuous variables
# "spearman" for rank-based monotonic correlation (with ordinal variables)
# Unfortunately, `rcorr` does not support Kendall's Tau
corr_mthd = "spearman"
corrMatrix <- 
  data %>% select(-study) %>% 
  as.matrix() %>% 
  rcorr(type = corr_mthd)

r_values <- corrMatrix$r
p_values <- corrMatrix$P
corrplot(r_values, p.mat = p_values, method = "circle", type="lower")
  

# Compare effectiveness correlations between groups
corr_mthd = "kendall"
effectivenessCorrelationComparison <-
  data %>%
  group_by(version) %>% 
  summarise(
    corr.intro = cor.test(effectiveness, intro, method = corr_mthd, exact = FALSE)$estimate,
    p.intro = cor.test(effectiveness, intro, method = corr_mthd, exact = FALSE)$p.value,
    corr.patterns = cor.test(effectiveness, expert.patterns, method = corr_mthd, exact = FALSE)$estimate,
    p.patterns = cor.test(effectiveness, expert.patterns, method = corr_mthd, exact = FALSE)$p.value,
    corr.spatterns = cor.test(effectiveness, expert.spatterns, method = corr_mthd, exact = FALSE)$estimate,
    p.spatterns = cor.test(effectiveness, expert.spatterns, method = corr_mthd, exact = FALSE)$p.value,
    corr.sbs = cor.test(effectiveness, expert.sbs, method = corr_mthd, exact = FALSE)$estimate,
    p.sbs = cor.test(effectiveness, expert.sbs, method = corr_mthd, exact = FALSE)$p.value,
    corr.programming = cor.test(effectiveness, yearsOfProgramming, method = corr_mthd, exact = FALSE)$estimate,
    p.programming = cor.test(effectiveness, yearsOfProgramming, method = corr_mthd, exact = FALSE)$p.value,
    corr.java = cor.test(effectiveness, expert.java, method = corr_mthd, exact = FALSE)$estimate,
    p.java = cor.test(effectiveness, expert.java, method = corr_mthd, exact = FALSE)$p.value,
    corr.web = cor.test(effectiveness, expert.web, method = corr_mthd, exact = FALSE)$estimate,
    p.web = cor.test(effectiveness, expert.web, method = corr_mthd, exact = FALSE)$p.value
  )

# --> for group 2, experience with patterns, service-based patterns, and Java was stronger correlated with effectiveness
# --> years of programming was correlated roughly equal in both groups

  
  