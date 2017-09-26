package paasta.delivery.pipeline.api.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * The type Job controller.
 */
@RestController
@RequestMapping(value = "/jobs")
class JobController {

    private final JobService jobService;


    /**
     * Instantiates a new Job controller.
     *
     * @param jobService the job service
     */
    @Autowired
    JobController(JobService jobService) {this.jobService = jobService;}


    /**
     * Create job custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     * @throws IOException the io exception
     */
    @PostMapping
    public CustomJob createJob(@RequestBody CustomJob customJob) throws IOException {
        return jobService.createJob(customJob);
    }


    /**
     * Update job custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     * @throws IOException the io exception
     */
    @PutMapping
    public CustomJob updateJob(@RequestBody CustomJob customJob) throws IOException {
        return jobService.updateJob(customJob);
    }


    /**
     * Delete job custom job.
     *
     * @param id the id
     * @return the custom job
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/{id:.+}", method = RequestMethod.DELETE)
    public CustomJob deleteJob(@PathVariable("id") String id) throws IOException {
        return jobService.deleteJob(id);
    }


    /**
     * Trigger job custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     */
    @RequestMapping(value = "/trigger", method = RequestMethod.POST)
    public CustomJob triggerJob(@RequestBody CustomJob customJob) {
        return jobService.triggerJob(customJob, new JobHistory());
    }


    /**
     * Stop job custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public CustomJob stopJob(@RequestBody CustomJob customJob) throws IOException {
        return jobService.stopJob(customJob);
    }


    /**
     * Gets job log.
     *
     * @param id        the id
     * @param jobNumber the job number
     * @return the job log
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/{id:.+}/log/{jobNumber:.+}", method = RequestMethod.GET)
    public CustomJob getJobLog(@PathVariable("id") int id, @PathVariable("jobNumber") int jobNumber) throws IOException {
        return jobService.getJobLog(id, jobNumber);
    }


    /**
     * Gets job status.
     *
     * @param id        the id
     * @param jobNumber the job number
     * @return the job status
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/{id:.+}/status/{jobNumber:.+}", method = RequestMethod.GET)
    public CustomJob getJobStatus(@PathVariable("id") int id, @PathVariable("jobNumber") int jobNumber) throws IOException {
        return jobService.getJobStatus(id, jobNumber);
    }


    /**
     * Replicate job custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/replicate", method = RequestMethod.POST)
    public CustomJob replicateJob(@RequestBody CustomJob customJob) throws IOException {
        return jobService.replicateJob(customJob);
    }


    /**
     * Rearrange job order custom job.
     *
     * @param customJob the custom job
     * @return the custom job
     */
    @RequestMapping(value = "/rearrange-job-order", method = RequestMethod.POST)
    public CustomJob rearrangeJobOrder(@RequestBody CustomJob customJob) {
        // TODO
        return jobService.rearrangeJobOrder(customJob);
    }

}
