import { Injectable } from '@nestjs/common';
import {
  AddLicencesForStudentUseCase,
  createAddLicenceForStudentUseCase,
} from '@licence-management/usecases/add-licences-for-student-use-case';
import { InMemoryRepositoryService } from '../../../licences/infrastructure/repository/in-memory-repository.service';

@Injectable()
export class AddLicenceForStudentUseCaseFactoryService {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}
  public createLicenceRequestUseCase(): AddLicencesForStudentUseCase {
    return createAddLicenceForStudentUseCase(this.licenceRepository);
  }
}
