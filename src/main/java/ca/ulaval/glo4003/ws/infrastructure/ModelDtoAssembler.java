package ca.ulaval.glo4003.ws.infrastructure;

import ca.ulaval.glo4003.ws.domain.transaction.Model;
import java.util.List;
import java.util.stream.Collectors;

public class ModelDtoAssembler {
  public List<Model> assembleModels(List<ModelDto> modelDtos) {
    return modelDtos.stream().map(this::assembleModel).collect(Collectors.toList());
  }

  private Model assembleModel(ModelDto modelDto) {
    return new Model(
        modelDto.name,
        modelDto.style,
        modelDto.efficiencyEquivalenceRate,
        modelDto.basePrice,
        modelDto.timeToProduce);
  }
}