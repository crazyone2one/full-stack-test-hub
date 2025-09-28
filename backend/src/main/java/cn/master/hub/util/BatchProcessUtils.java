package cn.master.hub.util;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Created by 11's papa on 2025/9/28
 */
public class BatchProcessUtils {
    private static int BATCH_SIZE = 200;

    public static void consumerByString(List<String> deleteIds, Consumer<List<String>> consumer) {
        if (deleteIds.size() > BATCH_SIZE) {
            int unProcessingCount = deleteIds.size();
            while (deleteIds.size() > BATCH_SIZE) {
                List<String> processingList = deleteIds.subList(0, BATCH_SIZE);
                consumer.accept(processingList);
                deleteIds.removeAll(processingList);
                if (deleteIds.size() == unProcessingCount) {
                    break;
                } else {
                    unProcessingCount = deleteIds.size();
                }
            }
            if (!deleteIds.isEmpty()) {
                consumer.accept(deleteIds);
            }
        } else {
            consumer.accept(deleteIds);
        }
    }
}
