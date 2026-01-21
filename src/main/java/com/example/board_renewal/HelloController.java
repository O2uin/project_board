package com.example.board_renewal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor // 1. 리포지토리를 자동으로 연결해줍니다.
public class HelloController {

    private final PostRepository postRepository; // 2. DB 연결 도구 선언 (ArrayList 삭제!)
    private final LikeService likeService;
    private final LikeRepository likeRepository;

    @GetMapping("/posts")
    public String getPosts(Model model) {
        // 3. DB에서 모든 게시글을 가져옵니다.
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "post-list";
    }

    @GetMapping("/posts/new")
    public String newPostForm(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginMember") == null) {
            return "redirect:/login"; // 로그인 안 했으면 폼도 안 보여줌!
        }
        return "post-form";
    }

    @PostMapping("/posts/new")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             HttpServletRequest request) { // ★ 세션을 쓰기 위해 추가

        // 1. 세션에서 로그인 정보 꺼내기
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginMember") == null) {
            // 로그인 안 한 사람이 주소창에 직접 쳐서 들어오면 로그인 페이지로 튕겨내기
            return "redirect:/login";
        }

        // 2. 세션에 담긴 회원 객체 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 3. 새로운 Post 객체 생성 (작성자 이름 포함)
        Post post = new Post(title, content);
        post.setAuthor(loginMember.getName()); // ★ 로그인한 사람의 이름을 작성자로!

        //글에 작성자 id저장
        post.setMemberId(loginMember.getId());

        // 4. DB에 저장
        postRepository.save(post);

        return "redirect:/posts";
    }

    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam Long id) {
        postRepository.deleteById(id);
        return "redirect:/posts";
    }
    @GetMapping("/posts/detail")
    public String postDetail(@RequestParam("id") Long id, HttpSession session, Model model) {
        // 1. DB에서 게시글 조회
        Post post = postRepository.findById(id).orElse(null);

        if (post == null) {
            return "redirect:/posts"; // 글 없으면 목록으로
        }

        // 2. 조회수 증가 로직 (기존에 있던 것 유지)
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post); // DB 반영

        // 3. 좋아요 여부 확인 (아까 만든 로직 합치기)
        boolean isLiked = false;
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            isLiked = likeService.isLiked(loginMember.getId(), id);
        }
        long likeCount = likeRepository.countByPost(post);

        model.addAttribute("post", post);
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("likeCount", likeCount);

        // 4. 파일명 정확히 리턴
        return "post-detail";
    }

    // 1. 수정 화면 띄우기
    @GetMapping("/posts/edit")
    public String editPostForm(@RequestParam Long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow(); // ID로 기존 글 찾기
        model.addAttribute("post", post); // 찾은 글을 수정 페이지로 전달
        return "post-edit";
    }

    // 2. 수정 내용 반영하기
    @PostMapping("/posts/edit")
    public String editPost(@RequestParam Long id, @RequestParam String title, @RequestParam String content) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title); // 새로운 제목 세팅
        post.setContent(content); // 새로운 내용 세팅
        postRepository.save(post); // DB에 업데이트 (ID가 같으면 덮어쓰기 됩니다)
        return "redirect:/posts";
    }

}