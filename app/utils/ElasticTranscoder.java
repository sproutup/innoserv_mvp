package utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobPlaylist;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.Job;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import play.Application;
import play.Logger;
import plugins.S3Plugin;

public class ElasticTranscoder {

    //------------------------------------------------------------------------------------------
    //				Audio and video Elestic transcoder preset codecs defined in AWS
    //------------------------------------------------------------------------------------------
    //  Name:							ID:						Container:	Description:
    //  System preset: HLS_2M			1351620000001-200010	ts			System preset: HLS 2M
    //  System preset: HLS_1_5M			1351620000001-200020	ts			System preset: HLS 1.5M
    //  System preset: HLS_1M			1351620000001-200030	ts			System preset: HLS 1M
    //  System preset: HLS_600k			1351620000001-200040	ts			System preset: HLS 600k
    //  System preset: HLS_400k			1351620000001-200050	ts			System preset: HLS 400k
    //------------------------------------------------------------------------------------------
    //
    //  System preset: HLS Video - 2M	1351620000001-200015	ts			System preset: HLS Video - 2M
    //  System preset: HLS Video - 1.5M	1351620000001-200025	ts			System preset: HLS Video - 1.5M
    //  System preset: HLS Video - 1M	1351620000001-200035	ts			System preset: HLS Video - 1M
    //  System preset: HLS Video - 600k	1351620000001-200045	ts			System preset: HLS Video - 600k
    //  System preset: HLS Video - 400k	1351620000001-200055	ts			System preset: HLS Video - 400k
    //------------------------------------------------------------------------------------------------------
    //  Name:							ID:						Container:	Description:
    //  System preset: Apple TV 2G		1351620000001-100050	mp4			System preset: Apple TV 2G
    //  System preset: Apple TV 3G		1351620000001-100060	mp4			System preset: Apple TV 3G, Roku HD/2 XD
    //  System preset: Audio AAC - 128k	1351620000001-100130	mp4			System preset: Audio AAC - 128 kilobits/second
    //  System preset: Audio AAC - 160k	1351620000001-100120	mp4			System preset: Audio AAC - 160 kilobits/second
    //  System preset: Audio AAC - 256k	1351620000001-100110	mp4			System preset: Audio AAC - 256 kilobits/second
    //  System preset: Audio AAC - 64k	1351620000001-100141	mp4			System preset: Audio AAC - 64 kilobits/second
    //  System preset: Audio MP3 - 128k	1351620000001-300040	mp3			System preset: Audio MP3 - 128 kilobits/second
    //  System preset: Audio MP3 - 160k	1351620000001-300030	mp3			System preset: Audio MP3 - 160 kilobits/second
    //  System preset: Audio MP3 - 192k	1351620000001-300020	mp3			System preset: Audio MP3 - 192 kilobits/second
    //  System preset: Audio MP3 - 320k	1351620000001-300010	mp3			System preset: Audio MP3 - 320 kilobits/second
    //  System preset: Generic 1080p	1351620000001-000001	mp4			System preset generic 1080p
    //  System preset: Generic 320x240	1351620000001-000061	mp4			System preset generic 320x240
    //  System preset: Generic 360p 16:91351620000001-000040	mp4			System preset generic 360p 16:9
    //  System preset: Generic 360p 4:3	1351620000001-000050	mp4			System preset generic 360p 4:3
    //  System preset: Generic 480p 16:91351620000001-000020	mp4			System preset generic 480p 16:9
    //  System preset: Generic 480p 4:3	1351620000001-000030	mp4			System preset generic 480p 4:3
    //  System preset: Generic 720p		1351620000001-000010	mp4			System preset generic 720p
    //  System preset: HLS 1.5M			1351620000001-200020	ts			System preset: HLS 1.5M
    //  System preset: HLS 1M			1351620000001-200030	ts			System preset: HLS 1M
    //  System preset: HLS 2M			1351620000001-200010	ts			System preset: HLS 2M
    //  System preset: HLS 400k			1351620000001-200050	ts			System preset: HLS 400k
    //  System preset: HLS 600k			1351620000001-200040	ts			System preset: HLS 600k
    //  System preset: HLS Audio - 160k	1351620000001-200060	ts			System Preset: HLS Audio 160 kilobits/second
    //  System preset: HLS Audio - 64k	1351620000001-200071	ts			System preset: HLS Audio 64 kilobits/second
    //  System preset: HLS Video - 1.5M	1351620000001-200025	ts			System preset: HLS Video - 1.5M
    //  System preset: HLS Video - 1M	1351620000001-200035	ts			System preset: HLS Video - 1M
    //  System preset: HLS Video - 2M	1351620000001-200015	ts			System preset: HLS Video - 2M
    //  System preset: HLS Video - 400k	1351620000001-200055	ts			System preset: HLS Video - 400k
    //  System preset: HLS Video - 600k	1351620000001-200045	ts			System preset: HLS Video - 600k
    //  System preset: KindleFire		1351620000001-100100	mp4			System preset: Kindle Fire
    //  System preset: KindleFireHD		1351620000001-100080	mp4			System preset: Kindle Fire HD
    //  System preset: KindleFireHD8.9	1351620000001-100090	mp4			System preset: Kindle Fire HD 8.9
    //  System preset: KindleFireHDX	1351620000001-100150	mp4			System preset: Kindle Fire HDX, Kindle Fire HDX 8.9
    //  System preset: Smooth 1.5M		1351620000001-400030	fmp4		System preset: Smooth 1.5M
    //  System preset: Smooth 1M		1351620000001-400040	fmp4		System preset: Smooth 1M
    //  System preset: Smooth 2M		1351620000001-400020	fmp4		System preset: Smooth 2M
    //  System preset: Smooth 3M		1351620000001-400010	fmp4		System preset: Smooth 3M
    //  System preset: Smooth 400k		1351620000001-400080	fmp4		System preset: Smooth 400k
    //  System preset: Smooth 500k		1351620000001-400070	fmp4		System preset: Smooth 500k
    //  System preset: Smooth 600k		1351620000001-400060	fmp4		System preset: Smooth 600k
    //  System preset: Smooth 800k		1351620000001-400050	fmp4		System preset: Smooth 800k
    //  System preset: Web				1351620000001-100070	mp4			System preset: Facebook, SmugMug, Vimeo, YouTube
    //  System preset: iPhone3GS		1351620000001-100030	mp4			System preset: iPhone 3GS
    //  System preset: iPhone4			1351620000001-100010	mp4			System preset: iPod touch 5G, 4G, iPad 1G, 2G
    //  System preset: iPhone4S			1351620000001-100020	mp4			System preset: iPhone 4s and above, iPad 3G and above, iPad mini, Samsung Galaxy S2/S3/Tab 2
    //  System preset: iPod Touch		1351620000001-100040	mp4			System preset: iPhone 1, 3, iPod classic
    //------------------------------------------------------------------------------------------------------


    private static final String PIPELINE_ID = "1423684927042-ajd6jz";

    // This is the name of the input key that you would like to transcode.
    // The input bucket is specified in the AWS Elastic Transcoder web interface
    private static String INPUT_KEY;

    //name of transcoded output files in the format of hls_2m_xxx
    private static String OUTPUT_KEY;

    // organize the segments in a folder that is named after the original input file name
    private static String OUTPUT_KEY_PREFIX;

    // HLS Presets that will be used to create an adaptive bitrate playlist.
    private static final String WEB_PRESET_ID           = "1351620000001-100070";
    private static final String HLS_2M_PRESET_ID 		= "1351620000001-200010";
    private static final String HLS_1_5M_PRESET_ID		= "1351620000001-200020";
    private static final String HLS_1M_PRESET_ID		= "1351620000001-200030";
    private static final String HLS_600k_PRESET_ID     	= "1351620000001-200040";
    private static final String HLS_400k_PRESET_ID     	= "1351620000001-200050";

    // HLS Segment duration that will be targeted.
    private static final String SEGMENT_DURATION = "2";

    // Create a job which outputs an HLS playlist for adaptive bitrate playback.
    // Must delete previous transcode file for successul pipeline job
    // @return Job that was created by Elastic Transcoder.
    // @throws Exception
    public static Job createElasticTranscoderHlsJob(String input_key, String output_key, String output_key_prefix) {
        String accessKey = S3Plugin.accessKey;
        String secretKey = S3Plugin.secretKey;
        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);

        // Clients are built using the default credentials provider chain.  This
        // will attempt to get your credentials in the following order:
        //      1. Environment variables (AWS_ACCESS_KEY and AWS_SECRET_KEY).
        //      2. Java system properties (AwsCredentials.properties).
        //      3. Instance profile credentials on EC2 instances.
        AmazonElasticTranscoder amazonElasticTranscoder = new AmazonElasticTranscoderClient();
        amazonElasticTranscoder.setRegion(usWest2);

        INPUT_KEY = input_key;
        OUTPUT_KEY = output_key;
        OUTPUT_KEY_PREFIX = output_key_prefix;
        // Setup the job input using the provided input key.
        JobInput input = new JobInput()
        .withKey(INPUT_KEY);

        // Setup the job outputs using the HLS presets.
        String outputKey = (OUTPUT_KEY);

        //setup encoding presets for HLS transcoding
        CreateJobOutput mp4 = new CreateJobOutput()
                .withKey(outputKey + "/" + outputKey + ".mp4")
                .withPresetId(WEB_PRESET_ID);

        CreateJobOutput webm = new CreateJobOutput()
                .withKey(outputKey + "/" + outputKey + ".webm")
                .withPresetId(WEB_PRESET_ID);

        CreateJobOutput hls2m = new CreateJobOutput()
                .withKey(outputKey + "/hls-2m-")
                .withPresetId(HLS_2M_PRESET_ID)
                .withSegmentDuration(SEGMENT_DURATION);

        CreateJobOutput hls15m = new CreateJobOutput()
                .withKey(outputKey + "/hls-15m-")
                .withPresetId(HLS_1_5M_PRESET_ID)
                .withSegmentDuration(SEGMENT_DURATION);

        CreateJobOutput hls1m = new CreateJobOutput()
                .withKey(outputKey + "/hls-1m-")
                .withPresetId(HLS_1M_PRESET_ID)
                .withSegmentDuration(SEGMENT_DURATION);

        CreateJobOutput hls600k = new CreateJobOutput()
                .withKey(outputKey + "/hls-600k-")
                .withPresetId(HLS_600k_PRESET_ID)
                .withSegmentDuration(SEGMENT_DURATION);

        CreateJobOutput hls400k = new CreateJobOutput()
                .withKey(outputKey + "/hls-400k-")
                .withPresetId(HLS_400k_PRESET_ID)
            .withSegmentDuration(SEGMENT_DURATION);

        List<CreateJobOutput> outputs = Arrays.asList(mp4, webm, hls2m, hls15m, hls1m, hls600k, hls400k);

        // Setup master playlist which can be used to play using adaptive bitrate.
        CreateJobPlaylist playlist = new CreateJobPlaylist()
                .withName(outputKey + "/" + outputKey)
                .withFormat("HLSv3")
                .withOutputKeys(hls2m.getKey(), hls15m.getKey(), hls1m.getKey(), hls600k.getKey(), hls400k.getKey());

        // Create the EC2 pipeline job.
        CreateJobRequest createJobRequest = new CreateJobRequest()
                .withPipelineId(PIPELINE_ID)
                .withInput(input)
                //.withOutputKeyPrefix(OUTPUT_KEY_PREFIX + outputKey)
                .withOutputKeyPrefix(OUTPUT_KEY_PREFIX)
                .withOutputs(outputs)
                .withPlaylists(playlist);

        return amazonElasticTranscoder.createJob(createJobRequest).getJob();
    }
}
