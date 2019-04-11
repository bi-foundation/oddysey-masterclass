package org.factomprotocol.oddysey;

import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Hello world!
 */
public class MasterclassHelloOddysey {

    // Courtesy private EC address on the testnet. Use faucet.factoid.org to top up using `EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv`
    protected static final String EC_SECRET_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_EC_SECRET_ADDRESS", "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH");
    protected static Address ADDRESS = new Address(EC_SECRET_ADDRESS);
    protected static final EntryApiImpl ENTRY_CLIENT = new EntryApiImpl();
    protected static final FactomdClientImpl FACTOMD_CLIENT = new FactomdClientImpl();
    protected static final OfflineWalletdClientImpl OFFLINE_WALLET_CLIENT = new OfflineWalletdClientImpl();

    static {
        FACTOMD_CLIENT.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        OFFLINE_WALLET_CLIENT.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, getProperties()));
        ENTRY_CLIENT.setFactomdClient(FACTOMD_CLIENT);
        ENTRY_CLIENT.setWalletdClient(OFFLINE_WALLET_CLIENT);
    }


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your team name:");

        String team = scanner.nextLine();
        System.out.println("Team is: " + team);
        System.out.println("Your message to all Oddysey teams:");
        String shout = scanner.nextLine();
        System.out.println("Standby. We are anchoring your shout out forever ;)\r\n");


        Chain chain = chain(team);
        CommitAndRevealChainResponse composeResponse = ENTRY_CLIENT.commitAndRevealChain(chain, ADDRESS).join();
        String chainId = composeResponse.getRevealResponse().getChainId();

        CommitAndRevealEntryResponse entryResponse = ENTRY_CLIENT.commitAndRevealEntry(entry(chainId, shout), ADDRESS).join();
        System.out.println(String.format("Hi '%s', your shout '%s' has been put into the factom testnet. Please give it time to anchor (10 minutes max)", team, shout));

        System.out.println(String.format("Want to look at the result? Goto: https://testnet.factoid.org/entry?hash=%s", entryResponse.getRevealResponse().getEntryHash()));

    }


    protected static Chain chain(String team) {
        List<String> externalIds = Arrays.asList(
                "Oddysey",
                "Factom",
                "Masterclass",
                team
        );

        Entry firstEntry = new Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent(String.format("Welcome %s to your own little chain!", team));

        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);
        return chain;
    }


    protected static Entry entry(String chainId, String shout) {
        List<String> externalIds = Arrays.asList("My shout out to the teams");

        Entry entry = new Entry();
        entry.setChainId(chainId);
        entry.setContent(shout);
        entry.setExternalIds(externalIds);
        return entry;
    }


    protected static Properties getProperties() {
        Properties properties = new Properties();
        InputStream is = MasterclassHelloOddysey.class.getClassLoader().getResourceAsStream("settings.properties");
        try {
            properties.load(is);
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }


}
