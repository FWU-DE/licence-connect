import { Injectable } from '@nestjs/common';
import { InMemoryRepositoryService } from '../repository/in-memory-repository.service';
import {
  RemoveLicencesForStudentUseCase,
  createSimpleRemoveLicenceForStudentUseCase,
} from '../../../usecases/remove-licences-for-student-use-case';

@Injectable()
export class RemoveLicenceForStudentUseCaseFactoryService {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}
  public createRemoveLicenceForStudentUseCase(): RemoveLicencesForStudentUseCase {
    return createSimpleRemoveLicenceForStudentUseCase(this.licenceRepository);
  }
}
