import { Injectable } from '@nestjs/common';
import { InMemoryRepositoryService } from '../../../licences/infrastructure/repository/in-memory-repository.service';
import {
  ReleaseLicencesForStudentUseCase,
  createReleaseLicenceForStudentUseCase,
} from '@vidis/usecases/release-licence-for-student-use-case';
import { BundeslandIdentificationString } from '@licences/domain/ferderal-state-id';

@Injectable()
export class ReleaseLicenceForStudentUseCaseFactoryService {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}
  public createReleaseLicenceForStudentUseCase(
    _ferderalStateIdentifier: BundeslandIdentificationString,
    _schoolIdentifier: string,
    _applicationId: string,
  ): ReleaseLicencesForStudentUseCase {
    return createReleaseLicenceForStudentUseCase();
  }
}
