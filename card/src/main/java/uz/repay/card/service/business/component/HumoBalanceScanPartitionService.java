package uz.repay.card.service.business.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;
import uz.repay.card.util.HumoBalanceScanConstants;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class HumoBalanceScanPartitionService {
    private final Set<Integer> assignedScanPartitions = ConcurrentHashMap.newKeySet();
    private final AtomicLong assignmentVersion = new AtomicLong();

    public void assign(Collection<TopicPartition> partitions) {
        if (partitions == null || partitions.isEmpty()) {
            return;
        }

        var changed = partitions.stream()
                .filter(this::isScanTopic)
                .map(TopicPartition::partition)
                .filter(assignedScanPartitions::add)
                .toList();

        if (!changed.isEmpty()) {
            var version = assignmentVersion.incrementAndGet();
            log.info("event=humo.balance.scan.partitions.assigned partitions={} assignedCount={}",
                    changed, assignedScanPartitions.size());
            log.debug("event=humo.balance.scan.partitions.version_changed version={}", version);
        }
    }

    public void revoke(Collection<TopicPartition> partitions) {
        if (partitions == null || partitions.isEmpty()) {
            return;
        }

        var changed = partitions.stream()
                .filter(this::isScanTopic)
                .map(TopicPartition::partition)
                .filter(assignedScanPartitions::remove)
                .toList();

        if (!changed.isEmpty()) {
            var version = assignmentVersion.incrementAndGet();
            log.info("event=humo.balance.scan.partitions.revoked partitions={} assignedCount={}",
                    changed, assignedScanPartitions.size());
            log.debug("event=humo.balance.scan.partitions.version_changed version={}", version);
        }
    }

    public List<Integer> assignedScanPartitions() {
        return assignedScanPartitions.stream()
                .sorted()
                .toList();
    }

    public boolean ownsCard(Long cardId) {
        return cardId != null && assignedScanPartitions.contains(partitionFor(cardId));
    }

    public boolean ownsCard(Long cardId, long expectedAssignmentVersion) {
        return assignmentVersion() == expectedAssignmentVersion && ownsCard(cardId);
    }

    public long assignmentVersion() {
        return assignmentVersion.get();
    }

    public int partitionFor(Long cardId) {
        if (cardId == null) {
            throw new IllegalArgumentException("cardId must not be null");
        }
        return Math.floorMod(cardId, HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC_PARTITIONS);
    }

    private boolean isScanTopic(TopicPartition partition) {
        return partition != null && HumoBalanceScanConstants.HUMO_BALANCE_SCAN_TOPIC.equals(partition.topic());
    }
}
