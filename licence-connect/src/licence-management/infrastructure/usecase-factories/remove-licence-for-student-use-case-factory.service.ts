import { Injectable } from '@nestjs/common';
import { InMemoryRepositoryService } from '../../../licences/infrastructure/repository/in-memory-repository.service';
import {
  RemoveLicencesForStudentUseCase,
  createRemoveLicenceForStudentUseCase,
} from '@licence-management/usecases/remove-licences-for-student-use-case';

@Injectable()
export class RemoveLicenceForStudentUseCaseFactoryService {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}
  public createRemoveLicenceForStudentUseCase(): RemoveLicencesForStudentUseCase {
    return createRemoveLicenceForStudentUseCase(this.licenceRepository);
  }
}
