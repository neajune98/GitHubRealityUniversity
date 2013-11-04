package processingLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import obj.Job;
import obj.Survey;
import ctrl.Controller;

/**
 * The Class ProcessJobs.
 */
public class ProcessJobs {

	/** The list of surveys. */
	private List<Survey> surveysList = Controller.getControllerInstance().getSurveysList();

	/** The list of eligible jobs *. */
	private List<Job> eligibleJobsList = new ArrayList<>();

	/** The surveys preferred job *. */
	private Job surveyPreferredJob;

	/**
	 * Do processing.
	 * 
	 * @return the list
	 */
	public List<Survey> doProcess() {
		// the next four statements are for debugging purposes only
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	    StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
	    String methodName = e.getClassName() + "." + e.getMethodName();
	    System.out.println("---Entering " + methodName);
		// end of debugging statement set - 4 lines in all

		for (Survey survey : surveysList) {
			surveyPreferredJob = Controller.getControllerInstance().getJob("id",
					Integer.toString(survey.getPreferredJob()));

			if (survey.getGPA() >= surveyPreferredJob.getGPA()) { // if the student's GPA is high enough
				survey.setAssignedJob(surveyPreferredJob.getID());
				Controller.getControllerInstance().updateSQLSurvey(survey);
			}
			// *********************************************************************************
			else { // otherwise student has some problems and he/she may not be able to have his/her dream job!

				// Get the jobs in the same category
				eligibleJobsList = getJobsInCategory(survey);

				// If no jobs, get jobs in same industry
				if (eligibleJobsList.size() < 1) {
						eligibleJobsList = getJobsInIndustry(survey);
				}
						// If no jobs, get jobs in same type
				else if (eligibleJobsList.size() < 1) {
							eligibleJobsList = getJobsInType(survey);
				}
							// If no jobs, get jobs in same GPA
				else if (eligibleJobsList.size() < 1){
								eligibleJobsList = getJobsInGPA(survey);
				}								
								// If no jobs, get lowest GPA jobs
				else if (eligibleJobsList.size() < 1){ // This will get the minimum number of jobs (
									eligibleJobsList = getLowestGPAJobs(survey);
				}						
	
				// Pick random job out of eligible jobs
				if (eligibleJobsList.size() > 0) {

					// Go through the list and pick a random job
					Random rndJob = new Random();
					int randomArrayNumber;

					// If there is only one choice, get it. Otherwise, get a
					// random job
					if (eligibleJobsList.size() < 2) {
						survey.setAssignedJob(eligibleJobsList.get(0).getID());
						Controller.getControllerInstance().updateSQLSurvey(survey);
					} else {
						randomArrayNumber = rndJob.nextInt(eligibleJobsList.size());
						survey.setAssignedJob(eligibleJobsList.get(randomArrayNumber).getID());
						Controller.getControllerInstance().updateSQLSurvey(survey);
//						Controller.getControllerInstance().refreshScreen();
						System.out.println("Seed: " + randomArrayNumber);
					}
				}
				Job job = Controller.getControllerInstance().getJob("id", Integer.toString(survey.getAssignedJob()));
				System.out.println("Job: " + job.getName());
				System.out.println("Job GPA: " + job.getGPA());
				System.out.println("Job Category: " + job.getCategory());
				System.out.println("Job Industry: " + job.getIndustry());
				System.out.println("Job Type: " + job.getType());
			} // end outer else loop

		} // end for loop

		// the next statement is for debugging purposes only
	    System.out.println("\n---Leaving " + methodName);
		// end of debugging statement set
		return surveysList;

	} // end doProcess() method

	/**
	 * Returns a list of eligible jobs based on Category.
	 * 
	 * @param survey
	 *            the survey being processed
	 * @return list of eligible jobs
	 */
	private List<Job> getJobsInCategory(Survey survey) {
		// the next four statements are for debugging purposes only
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	    StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
	    String methodName = e.getClassName() + "." + e.getMethodName();
	    System.out.println(methodName);
		// end of debugging statement set - 4 lines in all

		String category = surveyPreferredJob.getCategory();

		List<Job> preferredJobsList = checkGPA(
				Controller.getControllerInstance().searchJobsList("category", category), survey);

		return preferredJobsList;
	}

	/**
	 * Returns a list of eligible jobs based on Industry.
	 * 
	 * @param survey
	 *            the survey being processed
	 * @return list of eligible jobs
	 */
	private List<Job> getJobsInIndustry(Survey survey) {
		// the next four statements are for debugging purposes only
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	    StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
	    String methodName = e.getClassName() + "." + e.getMethodName();
	    System.out.println(methodName);
		// end of debugging statement set - 4 lines in all

		String industry = surveyPreferredJob.getIndustry();

		List<Job> lstPrefJobs = checkGPA(
				Controller.getControllerInstance().searchJobsList("industry", industry), survey);

		return lstPrefJobs;
	}

	/**
	 * Returns a list of eligible jobs based on Type.
	 * 
	 * @param survey
	 *            the survey being processed
	 * @return list of eligible jobs
	 */
	private List<Job> getJobsInType(Survey survey) {
		// the next four statements are for debugging purposes only
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	    StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
	    String methodName = e.getClassName() + "." + e.getMethodName();
	    System.out.println(methodName);
		// end of debugging statement set - 4 lines in all

		String type = surveyPreferredJob.getType();

		List<Job> lstPrefJobs = checkGPA(
				Controller.getControllerInstance().searchJobsList("type", type), survey);

		return lstPrefJobs;
	}

	/**
	 * Returns a list of eligible jobs based on GPA.
	 * 
	 * @param survey
	 *            the survey being processed
	 * @return list of eligible jobs
	 */
	private List<Job> getJobsInGPA(Survey survey) {
		// the next four statements are for debugging purposes only
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	    StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
	    String methodName = e.getClassName() + "." + e.getMethodName();
	    System.out.println(methodName);
		// end of debugging statement set - 4 lines in all

		int gpa = surveyPreferredJob.getGPA();

		List<Job> lstPrefJobs = checkGPA(
				Controller.getControllerInstance().searchJobsList("gpa", Integer.toString(gpa)),
				survey);

		return lstPrefJobs;
	}

	/**
	 * Returns a list of lowest GPA available jobs.
	 * 
	 * @param survey
	 *            the survey being processed
	 * @return list of eligible jobs
	 */
	private List<Job> getLowestGPAJobs(Survey survey) {
		// the next four statements are for debugging purposes only
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	    StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
	    String methodName = e.getClassName() + "." + e.getMethodName();
	    System.out.println(methodName);
		// end of debugging statement set - 4 lines in all
	    
		List<Job> lowestGPAJobsList = Controller.getControllerInstance().searchJobsList("gpa",
				Integer.toString(2));

		return lowestGPAJobsList;
	}

	/**
	 * Checks eligible jobs GPA against the survey GPA.
	 * 
	 * @param jobsList
	 *            : the list of jobs being checked
	 * @param survey
	 *            the survey being processed
	 * @return list of checked jobs
	 */
	private List<Job> checkGPA(List<Job> jobsList, Survey survey) {
		// the next four statements are for debugging purposes only
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	    StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
	    String methodName = e.getClassName() + "." + e.getMethodName();
	    System.out.println(methodName);
		// end of debugging statement set - 4 lines in all

		List<Job> matchedJobsList = new ArrayList<>();

		for (Job job : jobsList) {
			if (survey.getGPA() >= job.getGPA()) {
				matchedJobsList.add(job);
			}
		}
		return matchedJobsList;
	}

}