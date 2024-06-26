import { Injectable } from '@nestjs/common';
import {
  AddLicencesForStudentUseCase,
  createAddLicenceForStudentUseCase,
} from '@usecases/add-licences-for-student-use-case';
import { InMemoryRepositoryService } from '../repository/in-memory-repository.service';

@Injectable()
export class AddLicenceForStudentUseCaseFactoryService {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}
  public createLicenceRequestUseCase(): AddLicencesForStudentUseCase {
    return createAddLicenceForStudentUseCase(this.licenceRepository);
  }
}
