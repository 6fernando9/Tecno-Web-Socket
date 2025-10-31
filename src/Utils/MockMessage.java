package Utils;

import java.util.List;

public class MockMessage {

    public static List<String> obtenerListaMockMessage(){
        String emisor1 = "demo@gmail.com";
        String emisor2 = "salethDios@gmail.com";
        String message1 = "+OK 612 octetos\r\n" +
                "Return-Path: <" + emisor1 + ">\r\n" +
                "Received: by mail.tecnoweb.org.bo; Sat, 31 Aug 2024 12:00:00 -0400\r\n" +
                "Date: Fri, 29 Aug 2024 10:00:00 -0400\r\n" +
                "From: " + emisor1 + ">\r\n" +
                "Subject: CREATEUSER\r\n" +
                "\r\n" +
                "Estimado usuario, la plataforma estara en mantenimiento esta noche.\r\n" +
                ".";
        String message2 = "+OK 712 octetos\r\n" +
                "Return-Path: <" + emisor1 + ">\r\n" +
                "Received: by mail.tecnoweb.org.bo; Sat, 31 Aug 2024 12:00:00 -0400\r\n" +
                "Date: Fri, 30 Aug 2024 10:00:00 -0400\r\n" +
                "From: " + emisor1 + ">\r\n" +
                "Subject: UPDATEUSER\r\n" +
                "\r\n" +
                "Estimado usuario, la plataforma estara en mantenimiento esta noche.\r\n" +
                ".";
        String message3 = "+OK 912 octetos\r\n" +
                "Return-Path: <" + emisor2 + ">\r\n" +
                "Received: by mail.tecnoweb.org.bo; Sat, 31 Aug 2024 12:00:00 -0400\r\n" +
                "Date: Fri, 30 Aug 2024 10:00:00 -0400\r\n" +
                "From: " + emisor2 + ">\r\n" +
                "Subject: DELETEUSER\r\n" +
                "\r\n" +
                "Estimado usuario, la plataforma estara en mantenimiento esta noche.\r\n" +
                ".";
        return List.of(message1,message2,message3);
    }
}



