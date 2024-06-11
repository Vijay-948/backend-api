package backend.real_estate.backendapi.utils;

import java.util.Random;

public class CommonUtil {

    public static String generateOTP(int length){
        String numbers = "0123456789";
        Random random = new Random();
        char[] otp = new char[length];

        for(int i = 0; i < otp.length; i++){
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }

        return new String(otp);
    }
}
