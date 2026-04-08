import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostService } from './post.service';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PostService]
    });
    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should transform backend response string hashtags to array', () => {
    const mockFeed = [
      { id: 1, body: 'Hello #world', hashtags: 'world', author: {}, comments: [], likes: [] }
    ];

    service.getFeed().subscribe(posts => {
      expect(posts.length).toBe(1);
      expect(posts[0].hashtagList).toEqual(['world']);
    });

    const req = httpMock.expectOne('http://localhost:8081/api/posts');
    expect(req.request.method).toBe('GET');
    req.flush(mockFeed);
  });
});
