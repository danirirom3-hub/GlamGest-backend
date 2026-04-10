package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ServiceResponseDTO;
import java.util.List;

public interface GetAllServicesUseCase {

    List<ServiceResponseDTO> execute();
}
