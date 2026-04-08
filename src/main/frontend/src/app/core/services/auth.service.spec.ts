import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login optimally', () => {
    const mockCredentials = { email: 'test@enicar.ucar.tn', password: 'password123' };
    const mockResponse = { token: 'mock-jwt-token', user: { id: 1, email: 'test@enicar.ucar.tn', role: 'student' } };

    service.login(mockCredentials).subscribe(response => {
      expect(response.token).toBe('mock-jwt-token');
      expect(response.user.email).toBe('test@enicar.ucar.tn');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should clear token and emit null on logout', () => {
    spyOn(localStorage, 'removeItem');
    service.logout();
    expect(localStorage.removeItem).toHaveBeenCalledWith('enicar-token');
    expect(service.currentUser()).toBeNull();
  });
});
