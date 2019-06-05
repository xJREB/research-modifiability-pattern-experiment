########################################################################
# Experiment Results Evaluation
# 2018-07-19
# xJREB
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

# Read data ------------
data <- read.csv("data.csv")
str(data)
summary(data)

# Transform data to a better format for ggplot
dataDuration <- data %>%
  # only use participants with effectiveness >= 66%
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
  # only use participants with effectiveness >= 66%
  # filter(!is.na(ex2Duration)) %>%
  group_by(version) %>% 
  summarise(
    numParticipants = n(),
    avgSemester = mean(semester),
    # percentage of participants that attended the introductory presentation
    introAttendance = mean(intro),
    # AVG self-reported difficulty for exercise1 (1-10)
    avgDiffEx1 = mean(diff.ex1, na.rm = TRUE),
    # AVG self-reported difficulty for exercise2 (1-10)
    avgDiffEx2 = mean(diff.ex2, na.rm = TRUE),
    # AVG self-reported difficulty for exercise3 (1-10)
    avgDiffEx3 = mean(diff.ex3, na.rm = TRUE),
    # Combined AVG self-reported difficulty (1-10)
    avgDiff = (avgDiffEx1 + avgDiffEx2 + avgDiffEx3) / 3,
    # AVG programming experience in years
    avgYearsOfProgramming = mean(yearsOfProgramming),
    # AVG self-reported skill/experience with Java (1-10)
    avgJava = mean(expert.java),
    # AVG self-reported skill/experience with web development (1-10)
    avgWeb = mean(expert.web),
    # AVG self-reported skill/experience with service-based systems (1-10)
    avgSbs = mean(expert.sbs),
    # AVG self-reported skill/experience with design patterns (1-10)
    avgPatterns = mean(expert.patterns),
    # AVG self-reported skill/experience with service-based design patterns (1-10)
    avgSpatterns = mean(expert.spatterns),
    # Combined AVG self-reported skill/experience (1-10)
    avgSkill = (avgJava + avgWeb + avgSbs + avgPatterns + avgSpatterns) / 5,
    # AVG effectiveness in percent for the 3 exercises (0, 1/3, 2/3, 1)
    avgEffectiveness = mean(effectiveness),
    # Number of participants that solved exercise1
    solvedEx1 = sum(!is.na(ex1Duration)),
    # Number of participants that solved exercise2
    solvedEx2 = sum(!is.na(ex2Duration)),
    # Number of participants that solved exercise3
    solvedEx3 = sum(!is.na(ex3Duration)),
    # AVG duration for exercise1
    avgDurationEx1 = mean(ex1Duration, na.rm = TRUE),
    # AVG duration for exercise2
    avgDurationEx2 = mean(ex2Duration, na.rm = TRUE),
    # AVG duration for exercise3
    avgDurationEx3 = mean(ex3Duration, na.rm = TRUE)
  )
groupComparison$avgDuration <- aggregate(duration ~ version, dataDuration, mean)$duration

# --> groups are roughly equal w.r.t. skill/experience and effectiveness
# minor differences:
# - ~14% more attended the intro for version 1
# - version 2 participants had ~ half a year more programming experience
# - version 2 participants had ~ 0.7 points more in self-reported skill/experience (1-10)


# Duration analysis (efficiency) ------

meanValueToolTips <- dataDuration %>% 
  mutate(exercise = factor(exercise, levels = c(1,2,3), labels = c("#1: Process Abstraction", "#2: Service Facade", "#3: Event-Driven Messaging"))) %>% 
  group_by(version, exercise) %>% 
  select(version, exercise, duration) %>% 
  summarise(duration = round(mean(duration, na.rm = TRUE)))

# Create box plot to compare duration of versions
dataDuration %>%
  mutate(version = factor(version, levels = c(1,2), labels = c("#1 (no patterns)", "#2 (patterns)"))) %>%
  mutate(exercise = factor(exercise, levels = c(1,2,3), labels = c("#1: Process Abstraction", "#2: Service Facade", "#3: Event-Driven Messaging"))) %>% 
  ggplot(aes(x = version, y = duration, fill = exercise)) +
  geom_boxplot(na.rm = TRUE) +
  facet_grid(exercise ~ .) +
  labs(x = "Group / Version", y = "Duration in sec", fill = "Task Number") +
  theme(axis.text.x = element_text(size=12)) +
  geom_text(data = meanValueToolTips, aes(label = duration), nudge_x = -0.45)


# Calculate t-tests for mean effectiveness difference between versions
trimPercent = 0.0 # cut off the best and worst x percent
t.test(
  x = data %>%
    filter(version == 1) %>%
    select(effectiveness) %>% 
    Trim(trim = trimPercent, na.rm = TRUE),
  y = data %>% 
    filter(version == 2) %>%
    select(effectiveness) %>% 
    Trim(trim = trimPercent, na.rm = TRUE),
  var.equal = FALSE,
  conf.level = 0.99
)

# --> no significant p for all exercises at once (exNum = c(1,2,3)): 0.309
# --> highly significant p for exercises 2 and 3 together (exNum = c(2,3)): 0.004
# --> no significant p for ex 1 (exNum = c(1)): 0.442
# --> no significant p for ex 2 (exNum = c(2)): 0.119
# --> highly significant p for ex 3 (exNum = c(3)): 0.008  

# Calculate t-tests for mean duration difference between versions per exercise
exNum = c(1,2,3) # select exercises
trimPercent = 0.0 # cut off the best and worst x percent
t.test(
  x = dataDuration %>%
    filter(version == 1) %>%
    filter(exercise %in% exNum) %>%
    select(duration) %>% 
    Trim(trim = trimPercent, na.rm = TRUE),
  y = dataDuration %>% 
    filter(version == 2) %>%
    filter(exercise %in% exNum) %>%
    select(duration) %>% 
    Trim(trim = trimPercent, na.rm = TRUE),
  var.equal = FALSE,
  conf.level = 0.99
)

# --> no significant p for all exercises at once (exNum = c(1,2,3)): 0.309
# --> highly significant p for exercises 2 and 3 together (exNum = c(2,3)): 0.004
# --> no significant p for ex 1 (exNum = c(1)): 0.442
# --> no significant p for ex 2 (exNum = c(2)): 0.119
# --> highly significant p for ex 3 (exNum = c(3)): 0.008


# Calculate correlation between version and duration
exNum = c(1,2,3) # select exercises
dataDuration %>%
  filter(exercise %in% exNum) %>%
  mutate(exercise = as.integer(exercise)) %>% 
  summarise(
    correlation = cor.test(version, duration, method = "spearman", exact = FALSE)$estimate,
    p.value = cor.test(version, duration, method = "spearman", exact = FALSE)$p.value
  )
# --> no significant correlation for all exercises at once (r = -0.174, p = 0.098)
# --> highly significant medium correlation for exercises 2 and 3 together (r = -0.469, p = 0.002)
# --> no significant correlation for ex 1 (r = 0.094, p = 0.527) and ex 2 (r = -0.285, p = 0.157)
# --> highly significant strong correlation for ex 3 (r = -0.756, p = 0.0004)


# Correlation matrix for exploration --------

# "pearson" for linear correlation of continuous variables
# "spearman" for rank-based monotonic correlation (with ordinal variables)
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

# --> for group 2, skill and experience with patterns, service-based patterns, and Java was stronger correlated with effectiveness
# --> years of programming was correlated roughly equal in both groups



  
  