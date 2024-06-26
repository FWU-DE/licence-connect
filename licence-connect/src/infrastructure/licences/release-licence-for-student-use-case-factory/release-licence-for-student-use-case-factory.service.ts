import { Injectable } from '@nestjs/common';
import { BundeslandIdentificationString } from '@domain/request-from-vidis-core';
import { InMemoryRepositoryService } from '../repository/in-memory-repository.service';
import {
  ReleaseLicencesForStudentUseCase,
  createReleaseLicenceForStudentUseCase,
} from '@usecases/release-licence-for-student-use-case';

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
