package com.stupor.listenerdetector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FarvaterDTO {
    private String radius;
    private String longitude;
    private String latitude;
    private String ip;
    private String port;
    private String publicId;
}
