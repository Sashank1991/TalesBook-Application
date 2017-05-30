package cmpe277project.nk.org.a277_socialmobileapp;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by narasakumar on 5/22/17.
 */

public class InsertImageToS3 extends AsyncTask<Void,Void,String> {

    public String s3URL=new String();
    final String TAG="InsertImageToS3";
    public File file;

    public InsertImageToS3(File file) {
        super();
        // do stuff
        this.file=file;

    }

    @Override
    protected String doInBackground(Void... params) {

        final String MY_ACCESS_KEY_ID="AKIAI5ZVW6YBMCMHVT6A", MY_SECRET_KEY="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";
        final String PICTURE_NAME= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date());

        AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials( MY_ACCESS_KEY_ID, MY_SECRET_KEY ) );
        Region usWest2 = Region.getRegion(Regions.US_WEST_1);
        s3Client.setRegion(usWest2);


        PutObjectRequest por = new PutObjectRequest( "vrbtestcmpe", PICTURE_NAME, file);
        por.setCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject( por );

        Log.d(TAG,"============URL FROM S3============");
        Log.d(TAG,s3Client.getResourceUrl("vrbtestcmpe", PICTURE_NAME));

        s3URL=s3Client.getResourceUrl("vrbtestcmpe", PICTURE_NAME);
        return s3URL;
    }

    @Override
    protected void onPostExecute (String result) {

        super.onPostExecute(result);

    }
}