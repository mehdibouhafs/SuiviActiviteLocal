package munisys.net.ma.suiviactivite.mail;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Properties;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by mehdibouhafs on 24/07/2017.
 */

public class Mail {

    public static Intent sendMail(String fileName){

        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Android/data/munisys.net.ma.suiviactivite/files/"+fileName);
       // Log.e("realPath","file/data/data/munisys.net.ma.suiviactivite/app_excels/myExcel.xls");
        //Log.e("filelocation",filelocation.getPath());

        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Bonjour, "+"\n\n vous trouverez ci-joint mes dernieres interventions"+"\n \n \n Cordialement,");
        String to[] = {"mehdibouhafs@hotmail.fr"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "mon Intervention");
        return emailIntent;
    }

    public static Message sendMail2(String object,String fileName) {

        final String username = "mehdibouhafs17@gmail.com";
        final String password = "Bouhafsmehdi2";
        String file = "/storage/emulated/0/Android/data/munisys.net.ma.suiviactivite/files/"+fileName;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("boukal01@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("mehdibouhafs@hotmail.fr"));
            message.setSubject(object);
            message.setText("Hello,"
                    + "\n\n Vous trouverez ci-joint mes interventions!");

            MimeBodyPart messageBodyPart;
            Multipart multipart = new MimeMultipart();

            messageBodyPart = new MimeBodyPart();

            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            return message;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }
}