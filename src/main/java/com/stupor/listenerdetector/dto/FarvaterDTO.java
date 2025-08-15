package com.stupor.listenerdetector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarvaterDTO {
    private String ip;
    private String latitude;
    private String longitude;
    private String port;
    private String public_id;
    private String radius;
}
