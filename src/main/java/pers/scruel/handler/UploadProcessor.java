package pers.scruel.handler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pers.scruel.gui.TipsFrame;
import pers.scruel.listener.PasteAction;
import pers.scruel.thread.UploadThread;
import pers.scruel.util.IOUtils;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Scruel Tao <scruel@vip.qq.com>
 */
@SuppressWarnings("unchecked")
public class UploadProcessor extends BaseProcessor {

  public UploadProcessor(TipsFrame tipsFrame) {
    super(tipsFrame, UploadThread.class);
    tipsFrame.initJlabelTitle("uploading");
    this.addActionListener(new PasteAction(tipsFrame));
  }

  @Override
  void htmlProcess(String data) throws Exception {
    Document doc = Jsoup.parse(data);
    Elements elements = doc.select("img");
    updateActionSum(elements.size());
    for (Element element : elements) {
      String filePath = element.attr("src");
      // new Thread(() -> QiNiuUtils.fileUpload(new File(filePath))).start();
      if (filePath.matches("[a-zA-Z]:.*")) {
        startThread(new File(filePath));
      }
      else if (filePath.startsWith("http")) {
        try {
          startThread(new URL(filePath));
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
      else {
        notifyActionSucceed();
      }
    }
  }

  @Override
  void imageProcess(Image data) throws Exception {
    byte[] imgBytes = IOUtils.getImgBytes(data);
    updateActionSum(1);
    startThread(imgBytes);
  }

  @Override
  void fileListProcess(List<File> data) throws Exception {
    updateActionSum(data.size());
    for (File file : data) {
      startThread(file);
    }
  }
}
