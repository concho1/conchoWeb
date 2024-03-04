package org.example.conchoweb.member.service.imgLogic;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import org.example.conchoweb.DriveQuickstart;
import org.example.conchoweb.member.model.MemberDAO;
import org.example.conchoweb.member.model.MemberDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FileDownloadLogic{
    private final MemberDAO memberDAO;
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public FileDownloadLogic(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    // 싱글톤 패턴 메서드 이용
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
        return FileLogic.getCredential();
    }

    // 해당하는 폴더 id 에서 이미지 4장 다운로드
    public List<String> getDriveFileLinks(String memberEmail) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        String folderId = "";

        Optional<MemberDTO> memberDTO = memberDAO.findUserByEmail(memberEmail);
        if (memberDTO.isPresent()) {
            MemberDTO member = memberDTO.get();
            folderId = member.getFolderId();
        }

        if (folderId.isEmpty()) {
            return new ArrayList<>(); // 폴더 ID가 없으면 빈 리스트 반환
        }

        List<String> fileLinks = new ArrayList<>();
        FileList result = service.files().list()
                .setQ("'" + folderId + "' in parents and mimeType contains 'image/'")
                .setSpaces("drive")
                .setFields("files(id, webViewLink)")
                .setPageSize(4) // 상위 4개 파일의 링크만 조회
                .execute();

        System.out.println(folderId);
        System.out.println(memberEmail);
        List<com.google.api.services.drive.model.File> files = result.getFiles();
        if (!files.isEmpty()) {
            for (com.google.api.services.drive.model.File file : files) {
                // 파일의 ID를 사용하여 웹 페이지에 직접 표시 가능한 이미지 URL 생성
                //System.out.println(file.getId());
                String directLink = "https://drive.google.com/thumbnail?id=" + file.getId() + "&sz=w1080";
                //System.out.println(directLink);
                fileLinks.add(directLink); // 생성된 직접 링크를 리스트에 추가
            }
        }

        return fileLinks;
    }

}
