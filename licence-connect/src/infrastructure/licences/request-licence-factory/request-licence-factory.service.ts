import { Injectable } from '@nestjs/common';
import { BundeslandIdentificationString } from 'domain/request-from-vidis-core';
import {
  RequestLicencesForStudentUseCase,
  createRequestLicencesForStudentUseCase,
} from '../../../usecases/request-licences-use-case';
import { InMemoryRepositoryService } from '../repository/in-memory-repository.service';

@Injectable()
export class RequestLicenceFactoryService {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}

  public createLicenceRequestUseCase(
    _ferderalStateIdentifier: BundeslandIdentificationString,
    _schoolIdentifier: string,
    _applicationId: string,
  ): RequestLicencesForStudentUseCase {
    return createRequestLicencesForStudentUseCase(this.licenceRepository);
  }
}
