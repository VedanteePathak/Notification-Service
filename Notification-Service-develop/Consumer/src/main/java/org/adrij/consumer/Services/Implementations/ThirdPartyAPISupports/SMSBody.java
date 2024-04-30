package org.adrij.consumer.Services.Implementations.ThirdPartyAPISupports;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SMSBody {
    private String deliverychannel;
    private Channels channels;
    private List<Destination> destination;
}



