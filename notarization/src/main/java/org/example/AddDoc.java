package org.example;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;

import javax.json.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class AddDoc {
    private static final Logger log = Logger.getLogger(AddDoc.class);

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public static void main(String[] args) throws Exception {
        // Load a file system based wallet for managing identities.
        Path walletPath = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);
        // load a CCP
        Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

        //计算文件hash
        Path path = Paths.get("doc","hello.txt");
        File file = new File(path.toString());
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] hex = DigestUtils.sha256(fileInputStream);
        String hex1 = DigestUtils.sha256Hex(fileInputStream);
        System.out.println(hex1);

        //获取用户东西的身份信息
        X509Identity userIdentity = (X509Identity)wallet.get("appUser");
        if (userIdentity == null) {
            System.out.println("an identity for the user does not exit in the wallet");
            return;
        }

        byte[] signInfo = RSASignUtils.sign(hex,userIdentity.getPrivateKey());

        String sign = RSASignUtils.toBase64(signInfo);

        //计算
//         create a gateway connection
        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork("mychannel");
            Channel channel = network.getChannel();
			Contract contract = network.getContract("fabcar");

			byte[] result;


			contract.submitTransaction("createDocRecord", hex1, sign);

			result = contract.evaluateTransaction("queryDocRecord", hex1);
//			byte[] signResult = RSASignUtils.fromBase64(result);

            System.out.println(new String(result));
            JSONObject obj = JSONObject.parseObject(new String(result));
            byte[] sg = RSASignUtils.fromBase64((String) obj.get("signature"));
			boolean valid = RSASignUtils.verify(hex,sg,userIdentity.getCertificate().getPublicKey());
			System.out.println(valid);
        }
    }
}
