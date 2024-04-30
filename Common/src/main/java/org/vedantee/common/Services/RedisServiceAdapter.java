package org.vedantee.common.Services;

import java.util.ArrayList;
import java.util.Set;

public interface RedisServiceAdapter {
    boolean contains(String phoneNumber);
    void addPhoneNumbersToBlacklist(ArrayList<String> phoneNumbers);
    void addPhoneNumbersToBlacklistInBatches(ArrayList<String> phoneNumbers);
    void removePhoneNumbersFromBlacklist(ArrayList<String> phoneNumbers);
    void removePhoneNumbersFromBlacklistInBatches(ArrayList<String> phoneNumbers);
    Set<String> getAllPhoneNumbersInBlacklist();
}
