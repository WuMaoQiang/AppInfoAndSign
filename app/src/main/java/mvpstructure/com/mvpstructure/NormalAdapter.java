package mvpstructure.com.mvpstructure;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * author:  cc
 * TODO
 * date:   On  2019/3/15
 */
public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.VH> {
    public static class VH extends RecyclerView.ViewHolder {
        public final TextView textView1;
        public final TextView textView2;
        public final TextView textView3;

        public VH(View v) {
            super(v);
            textView1 = (TextView) v.findViewById(R.id.textView1);
            textView2 = (TextView) v.findViewById(R.id.textView2);
            textView3 = (TextView) v.findViewById(R.id.textView3);

        }
    }

    private List<PackageInfo> mDatas;
    private Context context;
    private ClipboardManager myClipboard;

    public NormalAdapter(Context context, ClipboardManager myClipboard, List<PackageInfo> data) {
        this.myClipboard = myClipboard;
        this.context = context;
        this.mDatas = data;
    }

    private ClipData myClip;

    @Override
    public void onBindViewHolder(VH holder, int position) {

        String packagename = mDatas.get(position).packageName;

        PackageManager pManager = context.getPackageManager();
        ApplicationInfo applicationName = getApplicationName(packagename);
        CharSequence applicationLabel = pManager.getApplicationLabel(applicationName);

        String certificateSHA1Fingerprint = getCertificateSHA1Fingerprint(context, packagename);


        holder.textView1.setText(packagename);
        holder.textView2.setText(certificateSHA1Fingerprint.replace(":", "").toLowerCase());
        holder.textView3.setText(applicationLabel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClip = ClipData.newPlainText("text", certificateSHA1Fingerprint.replace(":", "").toLowerCase());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_1, parent, false);
        return new VH(v);
    }


    public ApplicationInfo getApplicationName(String packname) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return applicationInfo;
    }

    //这个是获取SHA1的方法
    public static String getCertificateSHA1Fingerprint(Context context, String packageName) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取当前要获取SHA1值的包名，也可以用其他的包名，但需要注意，

        //返回包括在包中的签名信息
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            //获得包的所有内容信息类
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //签名信息
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        //将签名转换为字节数组流
        InputStream input = new ByteArrayInputStream(cert);
        //证书工厂类，这个类实现了出厂合格证算法的功能
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //X509证书，X.509是一种非常通用的证书格式
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            //加密算法的类，这里的参数可以使MD4,MD5等加密算法
            MessageDigest md = MessageDigest.getInstance("SHA1");
            //获得公钥
            byte[] publicKey = md.digest(c.getEncoded());
            //字节到十六进制的格式转换
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    //这里是将获取到得编码进行16进制转换
    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }
}