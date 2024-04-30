package org.adrij.consumer.Services.Implementations.ThirdPartyAPISupports;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Destination {
    private List<String> msisdn;
    private long correlationId;
}
